#!/bin/bash

echo ""
echo "Applying migration IdentificationDetailOptions"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /identificationDetailOptions                        controllers.leadtrustee.individual.IdentificationDetailOptionsController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /identificationDetailOptions                        controllers.leadtrustee.individual.IdentificationDetailOptionsController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeIdentificationDetailOptions                  controllers.leadtrustee.individual.IdentificationDetailOptionsController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeIdentificationDetailOptions                  controllers.leadtrustee.individual.IdentificationDetailOptionsController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "identificationDetailOptions.title = identificationDetailOptions" >> ../conf/messages.en
echo "identificationDetailOptions.heading = identificationDetailOptions" >> ../conf/messages.en
echo "identificationDetailOptions.idCard = ID Card" >> ../conf/messages.en
echo "identificationDetailOptions.passport = Passport" >> ../conf/messages.en
echo "identificationDetailOptions.checkYourAnswersLabel = identificationDetailOptions" >> ../conf/messages.en
echo "identificationDetailOptions.error.required = Select identificationDetailOptions" >> ../conf/messages.en

echo "Adding to UserAnswersEntryGenerators"
awk '/trait UserAnswersEntryGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryIdentificationDetailOptionsUserAnswersEntry: Arbitrary[(IdentificationDetailOptionsPage.type, JsValue)] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        page  <- arbitrary[IdentificationDetailOptionsPage.type]";\
    print "        value <- arbitrary[IdentificationDetailOptions].map(Json.toJson(_))";\
    print "      } yield (page, value)";\
    print "    }";\
    next }1' ../test/generators/UserAnswersEntryGenerators.scala > tmp && mv tmp ../test/generators/UserAnswersEntryGenerators.scala

echo "Adding to PageGenerators"
awk '/trait PageGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryIdentificationDetailOptionsPage: Arbitrary[IdentificationDetailOptionsPage.type] =";\
    print "    Arbitrary(IdentificationDetailOptionsPage)";\
    next }1' ../test/generators/PageGenerators.scala > tmp && mv tmp ../test/generators/PageGenerators.scala

echo "Adding to ModelGenerators"
awk '/trait ModelGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryIdentificationDetailOptions: Arbitrary[IdentificationDetailOptions] =";\
    print "    Arbitrary {";\
    print "      Gen.oneOf(IdentificationDetailOptions.values.toSeq)";\
    print "    }";\
    next }1' ../test/generators/ModelGenerators.scala > tmp && mv tmp ../test/generators/ModelGenerators.scala

echo "Adding to UserAnswersGenerator"
awk '/val generators/ {\
    print;\
    print "    arbitrary[(IdentificationDetailOptionsPage.type, JsValue)] ::";\
    next }1' ../test/generators/UserAnswersGenerator.scala > tmp && mv tmp ../test/generators/UserAnswersGenerator.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class/ {\
     print;\
     print "";\
     print "  def identificationDetailOptions: Option[AnswerRow] = userAnswers.get(IdentificationDetailOptionsPage) map {";\
     print "    x =>";\
     print "      AnswerRow(";\
     print "        HtmlFormat.escape(messages(\"identificationDetailOptions.checkYourAnswersLabel\")),";\
     print "        HtmlFormat.escape(messages(s\"identificationDetailOptions.$x\")),";\
     print "        routes.IdentificationDetailOptionsController.onPageLoad(CheckMode).url";\
     print "      )"
     print "  }";\
     next }1' ../app/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/utils/CheckYourAnswersHelper.scala

echo "Migration IdentificationDetailOptions completed"
