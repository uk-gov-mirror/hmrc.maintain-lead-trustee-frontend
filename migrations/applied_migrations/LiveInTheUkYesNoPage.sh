#!/bin/bash

echo ""
echo "Applying migration LiveInTheUkYesNoPage"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /liveInTheUkYesNoPage                        controllers.leadtrustee.individual.LiveInTheUkYesNoPageController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /liveInTheUkYesNoPage                        controllers.leadtrustee.individual.LiveInTheUkYesNoPageController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeLiveInTheUkYesNoPage                  controllers.leadtrustee.individual.LiveInTheUkYesNoPageController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeLiveInTheUkYesNoPage                  controllers.leadtrustee.individual.LiveInTheUkYesNoPageController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "liveInTheUkYesNoPage.title = liveInTheUkYesNoPage" >> ../conf/messages.en
echo "liveInTheUkYesNoPage.heading = liveInTheUkYesNoPage" >> ../conf/messages.en
echo "liveInTheUkYesNoPage.checkYourAnswersLabel = liveInTheUkYesNoPage" >> ../conf/messages.en
echo "liveInTheUkYesNoPage.error.required = Select yes if liveInTheUkYesNoPage" >> ../conf/messages.en

echo "Adding to UserAnswersEntryGenerators"
awk '/trait UserAnswersEntryGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryLiveInTheUkYesNoPageUserAnswersEntry: Arbitrary[(LiveInTheUkYesNoPagePage.type, JsValue)] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        page  <- arbitrary[LiveInTheUkYesNoPagePage.type]";\
    print "        value <- arbitrary[Boolean].map(Json.toJson(_))";\
    print "      } yield (page, value)";\
    print "    }";\
    next }1' ../test/generators/UserAnswersEntryGenerators.scala > tmp && mv tmp ../test/generators/UserAnswersEntryGenerators.scala

echo "Adding to PageGenerators"
awk '/trait PageGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryLiveInTheUkYesNoPagePage: Arbitrary[LiveInTheUkYesNoPagePage.type] =";\
    print "    Arbitrary(LiveInTheUkYesNoPagePage)";\
    next }1' ../test/generators/PageGenerators.scala > tmp && mv tmp ../test/generators/PageGenerators.scala

echo "Adding to UserAnswersGenerator"
awk '/val generators/ {\
    print;\
    print "    arbitrary[(LiveInTheUkYesNoPagePage.type, JsValue)] ::";\
    next }1' ../test/generators/UserAnswersGenerator.scala > tmp && mv tmp ../test/generators/UserAnswersGenerator.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class/ {\
     print;\
     print "";\
     print "  def liveInTheUkYesNoPage: Option[AnswerRow] = userAnswers.get(LiveInTheUkYesNoPagePage) map {";\
     print "    x =>";\
     print "      AnswerRow(";\
     print "        HtmlFormat.escape(messages(\"liveInTheUkYesNoPage.checkYourAnswersLabel\")),";\
     print "        yesOrNo(x),";\
     print "        routes.LiveInTheUkYesNoPageController.onPageLoad(CheckMode).url";\
     print "      )"
     print "  }";\
     next }1' ../app/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/utils/CheckYourAnswersHelper.scala

echo "Migration LiveInTheUkYesNoPage completed"
