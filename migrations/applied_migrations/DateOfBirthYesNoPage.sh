#!/bin/bash

echo ""
echo "Applying migration DateOfBirthYesNoPage"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /dateOfBirthYesNoPage                        controllers.leadtrustee.individual.DateOfBirthYesNoPageController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /dateOfBirthYesNoPage                        controllers.leadtrustee.individual.DateOfBirthYesNoPageController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeDateOfBirthYesNoPage                  controllers.leadtrustee.individual.DateOfBirthYesNoPageController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeDateOfBirthYesNoPage                  controllers.leadtrustee.individual.DateOfBirthYesNoPageController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "dateOfBirthYesNoPage.title = dateOfBirthYesNoPage" >> ../conf/messages.en
echo "dateOfBirthYesNoPage.heading = dateOfBirthYesNoPage" >> ../conf/messages.en
echo "dateOfBirthYesNoPage.checkYourAnswersLabel = dateOfBirthYesNoPage" >> ../conf/messages.en
echo "dateOfBirthYesNoPage.error.required = Select yes if dateOfBirthYesNoPage" >> ../conf/messages.en

echo "Adding to UserAnswersEntryGenerators"
awk '/trait UserAnswersEntryGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryDateOfBirthYesNoPageUserAnswersEntry: Arbitrary[(DateOfBirthYesNoPagePage.type, JsValue)] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        page  <- arbitrary[DateOfBirthYesNoPagePage.type]";\
    print "        value <- arbitrary[Boolean].map(Json.toJson(_))";\
    print "      } yield (page, value)";\
    print "    }";\
    next }1' ../test/generators/UserAnswersEntryGenerators.scala > tmp && mv tmp ../test/generators/UserAnswersEntryGenerators.scala

echo "Adding to PageGenerators"
awk '/trait PageGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryDateOfBirthYesNoPagePage: Arbitrary[DateOfBirthYesNoPagePage.type] =";\
    print "    Arbitrary(DateOfBirthYesNoPagePage)";\
    next }1' ../test/generators/PageGenerators.scala > tmp && mv tmp ../test/generators/PageGenerators.scala

echo "Adding to UserAnswersGenerator"
awk '/val generators/ {\
    print;\
    print "    arbitrary[(DateOfBirthYesNoPagePage.type, JsValue)] ::";\
    next }1' ../test/generators/UserAnswersGenerator.scala > tmp && mv tmp ../test/generators/UserAnswersGenerator.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class/ {\
     print;\
     print "";\
     print "  def dateOfBirthYesNoPage: Option[AnswerRow] = userAnswers.get(DateOfBirthYesNoPagePage) map {";\
     print "    x =>";\
     print "      AnswerRow(";\
     print "        HtmlFormat.escape(messages(\"dateOfBirthYesNoPage.checkYourAnswersLabel\")),";\
     print "        yesOrNo(x),";\
     print "        routes.DateOfBirthYesNoPageController.onPageLoad(CheckMode).url";\
     print "      )"
     print "  }";\
     next }1' ../app/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/utils/CheckYourAnswersHelper.scala

echo "Migration DateOfBirthYesNoPage completed"
