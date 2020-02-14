#!/bin/bash

echo ""
echo "Applying migration UkCitizen"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /ukCitizen                        controllers.leadtrustee.individual.UkCitizenController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /ukCitizen                        controllers.leadtrustee.individual.UkCitizenController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeUkCitizen                  controllers.leadtrustee.individual.UkCitizenController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeUkCitizen                  controllers.leadtrustee.individual.UkCitizenController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "ukCitizen.title = ukCitizen" >> ../conf/messages.en
echo "ukCitizen.heading = ukCitizen" >> ../conf/messages.en
echo "ukCitizen.checkYourAnswersLabel = ukCitizen" >> ../conf/messages.en
echo "ukCitizen.error.required = Select yes if ukCitizen" >> ../conf/messages.en

echo "Adding to UserAnswersEntryGenerators"
awk '/trait UserAnswersEntryGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryUkCitizenUserAnswersEntry: Arbitrary[(UkCitizenPage.type, JsValue)] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        page  <- arbitrary[UkCitizenPage.type]";\
    print "        value <- arbitrary[Boolean].map(Json.toJson(_))";\
    print "      } yield (page, value)";\
    print "    }";\
    next }1' ../test/generators/UserAnswersEntryGenerators.scala > tmp && mv tmp ../test/generators/UserAnswersEntryGenerators.scala

echo "Adding to PageGenerators"
awk '/trait PageGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryUkCitizenPage: Arbitrary[UkCitizenPage.type] =";\
    print "    Arbitrary(UkCitizenPage)";\
    next }1' ../test/generators/PageGenerators.scala > tmp && mv tmp ../test/generators/PageGenerators.scala

echo "Adding to UserAnswersGenerator"
awk '/val generators/ {\
    print;\
    print "    arbitrary[(UkCitizenPage.type, JsValue)] ::";\
    next }1' ../test/generators/UserAnswersGenerator.scala > tmp && mv tmp ../test/generators/UserAnswersGenerator.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class/ {\
     print;\
     print "";\
     print "  def ukCitizen: Option[AnswerRow] = userAnswers.get(UkCitizenPage) map {";\
     print "    x =>";\
     print "      AnswerRow(";\
     print "        HtmlFormat.escape(messages(\"ukCitizen.checkYourAnswersLabel\")),";\
     print "        yesOrNo(x),";\
     print "        routes.UkCitizenController.onPageLoad(CheckMode).url";\
     print "      )"
     print "  }";\
     next }1' ../app/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/utils/CheckYourAnswersHelper.scala

echo "Migration UkCitizen completed"
