package net.mauhiz.repocleaner

import java.io.IOException
import java.nio.charset.StandardCharsets
import java.nio.file.{DirectoryStream, Files, Path, Paths}
import java.security.MessageDigest
import java.util.concurrent.TimeUnit

import org.apache.commons.codec.binary.Hex
import org.apache.commons.io.FileUtils
import org.apache.commons.lang3.StringUtils

object MvnRepoCleaner extends App {

  val repoCleaner: MvnRepoCleaner = new MvnRepoCleaner(args(0))
  repoCleaner.run()

  private def getSha1Digest: MessageDigest = MessageDigest.getInstance("SHA-1")

  private def readFirstLine(file: Path): String = {
    val reader = Files.newBufferedReader(file, StandardCharsets.UTF_8)
    try {
      reader.readLine
    } finally {
      reader.close()
    }
  }

  def forDirectoryContents(file: Path)(f: Path => Unit): Unit = {
    val directoryStream: DirectoryStream[Path] = Files.newDirectoryStream(file)
    try {
      val it = directoryStream.iterator()
      while (it.hasNext) f(it.next())
    } finally {
      directoryStream.close()
    }
  }
}


class MvnRepoCleaner(repoRootStr: String,
                     deleteInvalidSha1: Boolean = true,
                     deleteOldSnapshot: Boolean = true,
                     deleteOrphanArtifact: Boolean = true,
                     deleteOrphanSha1: Boolean = true,
                     deleteTmpFile: Boolean = true) extends Runnable {

  import net.mauhiz.repocleaner.MvnRepoCleaner._

  private val repoRoot: Path = Paths.get(repoRootStr)
  final val MvnSnapshotName = "\\-SNAPSHOT[\\.\\-]".r
  final val MvnSnapshotTs = "\\-20\\d\\d[01]\\d[0-2]\\d\\.[0-2]\\d[0-5]\\d[0-5]\\d\\-".r
  final val SnapshotExpirationMs: Long = TimeUnit.DAYS.toMillis(15)

  def run(): Unit = recurseClean(repoRoot)


  private def checkSha1(sha1: String, jarFile: Path, sha1File: Path): Unit = {
    val input = Files.readAllBytes(jarFile)
    val messageDigest: MessageDigest = getSha1Digest
    val output = messageDigest.digest(input)
    val hash: String = new String(Hex.encodeHex(output))
    if (StringUtils.equals(sha1, hash)) {
      return
    }
    println(s"Reference: $sha1")
    println(s"Computed: $hash")
    if (deleteInvalidSha1) {
      Files.deleteIfExists(jarFile)
      Files.deleteIfExists(sha1File)
      println(s"Deleting $jarFile")
      println(s"Deleting $sha1File")
    }
    else {
      System.err.println(s"Corrupt file: $jarFile")
    }
  }

  private def handleDirectory(dir: Path): Unit = {
    val dirName = dir.toAbsolutePath.getFileName.toString
    if (".cache" != dirName && ".locks" != dirName) {
      forDirectoryContents(dir)(recurseClean)
      if (hasNoSubFolder(dir) && hasNoArtifact(dir)) {
        println(s"Deleting (almost) empty folder: $dir")
        FileUtils.deleteDirectory(dir.toFile)
      }
    }
  }

  private def handleRegularFile(file: Path): Unit = {
    val fileName = file.toAbsolutePath.getFileName.toString
    val ext = StringUtils.substringAfterLast(fileName, ".")
    if ("sha1" == ext) {
      val sha1 = readFirstLine(file)
      if (sha1 == null) {
        System.err.println(s"Invalid reference file: $file")
      }
      else {
        handleSha1(sha1, file, fileName)
      }
    }
    else if ("jar" == ext || "pom" == ext || "war" == ext || "zip" == ext) {
      val sha1File = file.resolveSibling(s"$fileName.sha1")
      if (Files.notExists(sha1File)) {
        if (deleteOrphanArtifact) {
          Files.delete(file)
          println(s"Deleting artifact with missing sha1 file: $file")
        }
        else {
          println(s"Artifact with missing sha1 file: $file")
        }
      }
      if (Files.exists(file) && isOldSnapshot(file, fileName)) {
        if (deleteOldSnapshot) {
          Files.delete(file)
          Files.deleteIfExists(sha1File)
          println(s"Deleting old snapshot: $file")
        }
        else {
          println(s"Oh, an old snapshot: $file")
        }
      }
    }
    else if ("tmp" == ext) {
      if (deleteTmpFile) {
        Files.deleteIfExists(file)
        println(s"Deleting TMP file: $file")
      }
      else {
        println(s"TMP file: $file")
      }
    }
  }

  private def handleSha1(pSha1: String, sha1File: Path, sha1FileName: String): Unit = {
    val jarFile = sha1File.resolveSibling(StringUtils.substringBeforeLast(sha1FileName, "."))
    if (Files.isRegularFile(jarFile)) {
      val sha1 = StringUtils.substringBefore(pSha1, " ")
      checkSha1(sha1, jarFile, sha1File)
    }
    else if (deleteOrphanSha1) {
      Files.deleteIfExists(sha1File)
      println(s"Deleting orphaned sha1 file: $sha1File")
    }
    else {
      println(s"Skipping non-existent hashed file: $jarFile")
    }
  }

  private def hasNoArtifact(file: Path): Boolean = {
    forDirectoryContents(file) {
      sub => if (Files.isRegularFile(sub)) {
        val filename = sub.toAbsolutePath.getFileName.toString
        if (filename.endsWith(".war") || filename.endsWith(".jar") || filename.endsWith(".pom") || filename.endsWith(".zip")) {
          return false
        }
      }
    }
    true
  }

  private def hasNoSubFolder(file: Path): Boolean = {
    forDirectoryContents(file) {
      sub => if (Files.isDirectory(sub)) {
        return false
      }
    }
    true
  }

  private def isOldSnapshot(file: Path, fileName: String): Boolean = {
    val isSnapshot: Boolean = MvnSnapshotName.findFirstIn(fileName).isDefined || MvnSnapshotTs.findFirstIn(fileName).isDefined
    isSnapshot && Files.getLastModifiedTime(file).toMillis < System.currentTimeMillis - SnapshotExpirationMs
  }

  private def recurseClean(file: Path): Unit = {
    if (Files.isDirectory(file)) {
      try {
        handleDirectory(file)
      }
      catch {
        case io: IOException =>
          System.err.println(s"Cannot browse: $file")
          io.printStackTrace()
      }
    }
    else if (Files.isRegularFile(file) && Files.isReadable(file)) {
      try {
        handleRegularFile(file)
      }
      catch {
        case io: IOException =>
          System.err.println(s"Could not handle file: $file")
          io.printStackTrace()
      }
    }
    else if (Files.exists(file)) {
      System.err.println(s"Wtf file: $file")
    }
    else {
    }
  }
}

