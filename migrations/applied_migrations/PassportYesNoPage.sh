#!/bin/bash

echo ""
echo "Applying migration PassportYesNoPage"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /passportYesNoPage                        controllers.leadtrustee.individual.PassportYesNoPageController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /passportYesNoPage                        controllers.leadtrustee.individual.PassportYesNoPageController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changePassportYesNoPage                  controllers.leadtrustee.individual.PassportYesNoPageController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changePassportYesNoPage                  controllers.leadtrustee.individual.PassportYesNoPageController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "passportYesNoPage.title = passportYesNoPage" >> ../conf/messages.en
echo "passportYesNoPage.heading = passportYesNoPage" >> ../conf/messages.en
echo "passportYesNoPage.checkYourAnswersLabel = passportYesNoPage" >> ../conf/messages.en
echo "passportYesNoPage.error.required = Select yes if passportYesNoPage" >> ../conf/messages.en

echo "Adding to UserAnswersEntryGenerators"
awk '/trait UserAnswersEntryGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryPassportYesNoPageUserAnswersEntry: Arbitrary[(PassportYesNoPagePage.type, JsValue)] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        page  <- arbitrary[PassportYesNoPagePage.type]";\
    print "        value <- arbitrary[Boolean].map(Json.toJson(_))";\
    print "      } yield (page, value)";\
    print "    }";\
    next }1' ../test/generators/UserAnswersEntryGenerators.scala > tmp && mv tmp ../test/generators/UserAnswersEntryGenerators.scala

echo "Adding to PageGenerators"
awk '/trait PageGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryPassportYesNoPagePage: Arbitrary[PassportYesNoPagePage.type] =";\
    print "    Arbitrary(PassportYesNoPagePage)";\
    next }1' ../test/generators/PageGenerators.scala > tmp && mv tmp ../test/generators/PageGenerators.scala

echo "Adding to UserAnswersGenerator"
awk '/val generators/ {\
    print;\
    print "    arbitrary[(PassportYesNoPagePage.type, JsValue)] ::";\
    next }1' ../test/generators/UserAnswersGenerator.scala > tmp && mv tmp ../test/generators/UserAnswersGenerator.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class/ {\
     print;\
     print "";\
     print "  def passportYesNoPage: Option[AnswerRow] = userAnswers.get(PassportYesNoPagePage) map {";\
     print "    x =>";\
     print "      AnswerRow(";\
     print "        HtmlFormat.escape(messages(\"passportYesNoPage.checkYourAnswersLabel\")),";\
     print "        yesOrNo(x),";\
     print "        routes.PassportYesNoPageController.onPageLoad(CheckMode).url";\
     print "      )"
     print "  }";\
     next }1' ../app/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/utils/CheckYourAnswersHelper.scala

echo "Migration PassportYesNoPage completed"
