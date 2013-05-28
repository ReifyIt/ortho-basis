/*      ____              ___                                           *\
**     / __ | ___  ____  /__/___      A library of building blocks      **
**    / __  / __ |/ ___|/  / ___|                                       **
**   / /_/ / /_/ /\__ \/  /\__ \      (c) 2012-2013 Reify It            **
**  |_____/\_____\____/__/\____/      http://basis.reify.it             **
\*                                                                      */

import sbt._
import sbt.Keys._
import sbt.Defaults.defaultSettings

object OrthoBasis extends Build {
  lazy val OrthoBasis = Project(
    id           = "ortho-basis",
    base         = file("."),
    settings     = baseSettings,
    dependencies =
      Seq(BasisConcurrent,
          BasisDispatch,
          BasisIO,
          BasisParallel,
          BasisPlatform),
    aggregate    =
      Seq(BasisConcurrent,
          BasisDispatch,
          BasisIO,
          BasisParallel,
          BasisPlatform))
  
  lazy val BasisConcurrent = Project(
    id           = "basis-concurrent",
    base         = file("basis-concurrent"),
    settings     = commonSettings ++ Seq(
      libraryDependencies <++= version { version =>
        Seq("it.reify" %% "basis-collections" % version,
            "it.reify" %% "basis-containers" % version,
            "it.reify" %% "basis-runtime" % version)
      }))
  
  lazy val BasisDispatch = Project(
    id           = "basis-dispatch",
    base         = file("basis-dispatch"),
    settings     = commonSettings ++ Seq(
      libraryDependencies <++= version { version =>
        Seq("it.reify" %% "basis-containers" % version,
            "it.reify" %% "basis-control" % version)
      }),
    dependencies = Seq(BasisConcurrent))
  
  lazy val BasisIO = Project(
    id           = "basis-io",
    base         = file("basis-io"),
    settings     = commonSettings ++ Seq(
      libraryDependencies <++= version { version =>
        Seq("it.reify" %% "basis-collections" % version,
            "it.reify" %% "basis-control" % version,
            "it.reify" %% "basis-memory" % version)
      }))
  
  lazy val BasisParallel = Project(
    id           = "basis-parallel",
    base         = file("basis-parallel"),
    settings     = commonSettings ++ Seq(
      libraryDependencies <++= version { version =>
        Seq("it.reify" %% "basis-collections" % version,
            "it.reify" %% "basis-control" % version,
            "it.reify" %% "basis-sequential" % version)
      }),
    dependencies = Seq(BasisDispatch))
  
  lazy val BasisPlatform = Project(
    id           = "basis-platform",
    base         = file("basis-platform"),
    settings     = commonSettings ++ Seq(
      libraryDependencies <++= version { version =>
        Seq("it.reify" %% "basis-memory" % version,
            "it.reify" %% "basis-util" % version)
      },
      unmanagedResources in Compile <++= target map { target =>
        Seq(target / "libbasis-platform.macosx-universal.jnilib")
      }),
    dependencies = Seq(BasisIO))
  
  lazy val baseSettings =
    defaultSettings ++
    Unidoc.settings ++
    projectSettings ++
    scalaSettings   ++
    docSettings     ++
    publishSettings
  
  lazy val commonSettings =
    baseSettings    ++
    compileSettings
  
  lazy val projectSettings = Seq(
    version      := "0.1-SNAPSHOT",
    organization := "it.reify",
    description  := "An experimental foundation library for Scala focussed on efficiency and clean design.",
    homepage     := Some(url("http://basis.reify.it")),
    licenses     := Seq("MIT" -> url("http://www.opensource.org/licenses/mit-license.php")),
    resolvers    += Resolver.sonatypeRepo("snapshots"))
  
  lazy val scalaSettings = Seq(
    scalaVersion   := "2.10.1",
    scalacOptions ++= Seq("-language:_", "-Yno-predef"))
  
  lazy val compileSettings = Seq(
    scalacOptions in Compile ++= Seq("-optimise", "-Xno-forwarders", "-Ywarn-all"),
    javacOptions  in Compile ++= Seq("-Xlint", "-XDignore.symbol.file"),
    libraryDependencies <+= (scalaVersion)("org.scala-lang" % "scala-reflect" % _ % "provided"))
  
  lazy val docSettings = Seq(
    scalacOptions in doc <++= (version, baseDirectory in LocalProject("ortho-basis")) map { (version, baseDirectory) =>
      val tagOrBranch = if (version.endsWith("-SNAPSHOT")) "master" else "v" + version
      val docSourceUrl = "https://github.com/reifyit/ortho-basis/tree/" + tagOrBranch + "€{FILE_PATH}.scala"
      Seq("-groups",
          "-implicits",
          "-diagrams",
          "-sourcepath", baseDirectory.getAbsolutePath,
          "-doc-source-url", docSourceUrl)
    })
  
  lazy val publishSettings = Seq(
    publishMavenStyle := true,
    publishTo <<= version { version =>
      val nexus = "https://oss.sonatype.org/"
      if (version.endsWith("SNAPSHOT")) Some("snapshots" at nexus + "content/repositories/snapshots")
      else Some("releases" at nexus + "service/local/staging/deploy/maven2")
    },
    pomIncludeRepository := (_ => false),
    pomExtra := {
      <scm>
        <url>git@github.com:reifyit/basis.git</url>
        <connection>scm:git:git@github.com:reifyit/basis.git</connection>
      </scm>
      <developers>
        <developer>
          <id>c9r</id>
          <name>Chris Sachs</name>
          <email>chris@reify.it</email>
        </developer>
      </developers>
    })
}
