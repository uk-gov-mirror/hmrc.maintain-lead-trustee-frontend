import sbt._

object AppDependencies {
  import play.core.PlayVersion

  val compile: Seq[ModuleID] = Seq(
    play.sbt.PlayImport.ws,
    "org.reactivemongo" %% "play2-reactivemongo"            % "0.18.8-play26",
    "uk.gov.hmrc"       %% "logback-json-logger"            % "4.8.0",
    "uk.gov.hmrc"       %% "govuk-template"                 % "5.54.0-play-26",
    "uk.gov.hmrc"       %% "play-health"                    % "3.15.0-play-26",
    "uk.gov.hmrc"       %% "play-ui"                        % "8.9.0-play-26",
    "uk.gov.hmrc"       %% "play-conditional-form-mapping"  % "1.2.0-play-26",
    "uk.gov.hmrc"       %% "bootstrap-play-26"              % "1.7.0",
    "uk.gov.hmrc"       %% "domain"                         % "5.8.0-play-26",
    "com.typesafe.play" %% "play-json-joda"                 % "2.7.4",
    "uk.gov.hmrc"       %% "play-whitelist-filter"          % "3.3.0-play-26",
    "org.typelevel"     %% "cats-core"                      % "2.0.0",
    "javax.inject"      %  "javax.inject"                   % "1"
  )

  val test: Seq[ModuleID] = Seq(
    "org.scalatest"               %% "scalatest"              % "3.0.7",
    "org.scalatestplus.play"      %% "scalatestplus-play"     % "3.1.2",
    "org.pegdown"                 %  "pegdown"                % "1.6.0",
    "org.jsoup"                   %  "jsoup"                  % "1.10.3",
    "com.typesafe.play"           %% "play-test"              % PlayVersion.current,
    "org.mockito"                 %  "mockito-all"            % "1.10.19",
    "org.scalacheck"              %% "scalacheck"             % "1.14.0",
    "wolfendale"                  %% "scalacheck-gen-regexp"  % "0.1.1",
    "com.github.tomakehurst"      % "wiremock-standalone"     % "2.17.0"
  ).map(_ % Test)

  def apply(): Seq[ModuleID] = compile ++ test
}
