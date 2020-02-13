#!/bin/bash

echo ""
echo "Applying migration NationalInsuranceNumberyesNoPage"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /nationalInsuranceNumberyesNoPage                        controllers.leadtrustee.individual.NationalInsuranceNumberyesNoPageController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /nationalInsuranceNumberyesNoPage                        controllers.leadtrustee.individual.NationalInsuranceNumberyesNoPageController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeNationalInsuranceNumberyesNoPage                  controllers.leadtrustee.individual.NationalInsuranceNumberyesNoPageController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeNationalInsuranceNumberyesNoPage                  controllers.leadtrustee.individual.NationalInsuranceNumberyesNoPageController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "nationalInsuranceNumberyesNoPage.title = nationalInsuranceNumberyesNoPage" >> ../conf/messages.en
echo "nationalInsuranceNumberyesNoPage.heading = nationalInsuranceNumberyesNoPage" >> ../conf/messages.en
echo "nationalInsuranceNumberyesNoPage.checkYourAnswersLabel = nationalInsuranceNumberyesNoPage" >> ../conf/messages.en
echo "nationalInsuranceNumberyesNoPage.error.required = Select yes if nationalInsuranceNumberyesNoPage" >> ../conf/messages.en

echo "Adding to UserAnswersEntryGenerators"
awk '/trait UserAnswersEntryGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryNationalInsuranceNumberyesNoPageUserAnswersEntry: Arbitrary[(NationalInsuranceNumberyesNoPagePage.type, JsValue)] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        page  <- arbitrary[NationalInsuranceNumberyesNoPagePage.type]";\
    print "        value <- arbitrary[Boolean].map(Json.toJson(_))";\
    print "      } yield (page, value)";\
    print "    }";\
    next }1' ../test/generators/UserAnswersEntryGenerators.scala > tmp && mv tmp ../test/generators/UserAnswersEntryGenerators.scala

echo "Adding to PageGenerators"
awk '/trait PageGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryNationalInsuranceNumberyesNoPagePage: Arbitrary[NationalInsuranceNumberyesNoPagePage.type] =";\
    print "    Arbitrary(NationalInsuranceNumberyesNoPagePage)";\
    next }1' ../test/generators/PageGenerators.scala > tmp && mv tmp ../test/generators/PageGenerators.scala

echo "Adding to UserAnswersGenerator"
awk '/val generators/ {\
    print;\
    print "    arbitrary[(NationalInsuranceNumberyesNoPagePage.type, JsValue)] ::";\
    next }1' ../test/generators/UserAnswersGenerator.scala > tmp && mv tmp ../test/generators/UserAnswersGenerator.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class/ {\
     print;\
     print "";\
     print "  def nationalInsuranceNumberyesNoPage: Option[AnswerRow] = userAnswers.get(NationalInsuranceNumberyesNoPagePage) map {";\
     print "    x =>";\
     print "      AnswerRow(";\
     print "        HtmlFormat.escape(messages(\"nationalInsuranceNumberyesNoPage.checkYourAnswersLabel\")),";\
     print "        yesOrNo(x),";\
     print "        routes.NationalInsuranceNumberyesNoPageController.onPageLoad(CheckMode).url";\
     print "      )"
     print "  }";\
     next }1' ../app/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/utils/CheckYourAnswersHelper.scala

echo "Migration NationalInsuranceNumberyesNoPage completed"
