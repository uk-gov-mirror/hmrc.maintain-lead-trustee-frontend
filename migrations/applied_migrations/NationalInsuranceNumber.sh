#!/bin/bash

echo ""
echo "Applying migration NationalInsuranceNumber"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /nationalInsuranceNumber                        controllers.leadtrustee.individual.NationalInsuranceNumberController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /nationalInsuranceNumber                        controllers.leadtrustee.individual.NationalInsuranceNumberController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeNationalInsuranceNumber                  controllers.leadtrustee.individual.NationalInsuranceNumberController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeNationalInsuranceNumber                  controllers.leadtrustee.individual.NationalInsuranceNumberController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "nationalInsuranceNumber.title = nationalInsuranceNumber" >> ../conf/messages.en
echo "nationalInsuranceNumber.heading = nationalInsuranceNumber" >> ../conf/messages.en
echo "nationalInsuranceNumber.checkYourAnswersLabel = nationalInsuranceNumber" >> ../conf/messages.en
echo "nationalInsuranceNumber.error.required = Enter nationalInsuranceNumber" >> ../conf/messages.en
echo "nationalInsuranceNumber.error.length = NationalInsuranceNumber must be 10 characters or less" >> ../conf/messages.en

echo "Adding to UserAnswersEntryGenerators"
awk '/trait UserAnswersEntryGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryNationalInsuranceNumberUserAnswersEntry: Arbitrary[(NationalInsuranceNumberPage.type, JsValue)] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        page  <- arbitrary[NationalInsuranceNumberPage.type]";\
    print "        value <- arbitrary[String].suchThat(_.nonEmpty).map(Json.toJson(_))";\
    print "      } yield (page, value)";\
    print "    }";\
    next }1' ../test/generators/UserAnswersEntryGenerators.scala > tmp && mv tmp ../test/generators/UserAnswersEntryGenerators.scala

echo "Adding to PageGenerators"
awk '/trait PageGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryNationalInsuranceNumberPage: Arbitrary[NationalInsuranceNumberPage.type] =";\
    print "    Arbitrary(NationalInsuranceNumberPage)";\
    next }1' ../test/generators/PageGenerators.scala > tmp && mv tmp ../test/generators/PageGenerators.scala

echo "Adding to UserAnswersGenerator"
awk '/val generators/ {\
    print;\
    print "    arbitrary[(NationalInsuranceNumberPage.type, JsValue)] ::";\
    next }1' ../test/generators/UserAnswersGenerator.scala > tmp && mv tmp ../test/generators/UserAnswersGenerator.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class/ {\
     print;\
     print "";\
     print "  def nationalInsuranceNumber: Option[AnswerRow] = userAnswers.get(NationalInsuranceNumberPage) map {";\
     print "    x =>";\
     print "      AnswerRow(";\
     print "        HtmlFormat.escape(messages(\"nationalInsuranceNumber.checkYourAnswersLabel\")),";\
     print "        HtmlFormat.escape(x),";\
     print "        routes.NationalInsuranceNumberController.onPageLoad(CheckMode).url";\
     print "      )"
     print "  }";\
     next }1' ../app/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/utils/CheckYourAnswersHelper.scala

echo "Migration NationalInsuranceNumber completed"
