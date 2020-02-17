#!/bin/bash

echo ""
echo "Applying migration NonUkAddress"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /nonUkAddress                        controllers.leadtrustee.individual.NonUkAddressController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /nonUkAddress                        controllers.leadtrustee.individual.NonUkAddressController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeNonUkAddress                  controllers.leadtrustee.individual.NonUkAddressController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeNonUkAddress                  controllers.leadtrustee.individual.NonUkAddressController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "nonUkAddress.title = nonUkAddress" >> ../conf/messages.en
echo "nonUkAddress.heading = nonUkAddress" >> ../conf/messages.en
echo "nonUkAddress.line1 = line1" >> ../conf/messages.en
echo "nonUkAddress.line2 = line2" >> ../conf/messages.en
echo "nonUkAddress.checkYourAnswersLabel = nonUkAddress" >> ../conf/messages.en
echo "nonUkAddress.error.line1.required = Enter line1" >> ../conf/messages.en
echo "nonUkAddress.error.line2.required = Enter line2" >> ../conf/messages.en
echo "nonUkAddress.error.line1.length = line1 must be line2 characters or less" >> ../conf/messages.en
echo "nonUkAddress.error.line2.length = line2 must be 100 characters or less" >> ../conf/messages.en

echo "Adding to UserAnswersEntryGenerators"
awk '/trait UserAnswersEntryGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryNonUkAddressUserAnswersEntry: Arbitrary[(NonUkAddressPage.type, JsValue)] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        page  <- arbitrary[NonUkAddressPage.type]";\
    print "        value <- arbitrary[NonUkAddress].map(Json.toJson(_))";\
    print "      } yield (page, value)";\
    print "    }";\
    next }1' ../test/generators/UserAnswersEntryGenerators.scala > tmp && mv tmp ../test/generators/UserAnswersEntryGenerators.scala

echo "Adding to PageGenerators"
awk '/trait PageGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryNonUkAddressPage: Arbitrary[NonUkAddressPage.type] =";\
    print "    Arbitrary(NonUkAddressPage)";\
    next }1' ../test/generators/PageGenerators.scala > tmp && mv tmp ../test/generators/PageGenerators.scala

echo "Adding to ModelGenerators"
awk '/trait ModelGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryNonUkAddress: Arbitrary[NonUkAddress] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        line1 <- arbitrary[String]";\
    print "        line2 <- arbitrary[String]";\
    print "      } yield NonUkAddress(line1, line2)";\
    print "    }";\
    next }1' ../test/generators/ModelGenerators.scala > tmp && mv tmp ../test/generators/ModelGenerators.scala

echo "Adding to UserAnswersGenerator"
awk '/val generators/ {\
    print;\
    print "    arbitrary[(NonUkAddressPage.type, JsValue)] ::";\
    next }1' ../test/generators/UserAnswersGenerator.scala > tmp && mv tmp ../test/generators/UserAnswersGenerator.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class/ {\
     print;\
     print "";\
     print "  def nonUkAddress: Option[AnswerRow] = userAnswers.get(NonUkAddressPage) map {";\
     print "    x =>";\
     print "      AnswerRow(";\
     print "        HtmlFormat.escape(messages(\"nonUkAddress.checkYourAnswersLabel\")),";\
     print "        HtmlFormat.escape(s\"${x.line1} ${x.line2}\"),";\
     print "        routes.NonUkAddressController.onPageLoad(CheckMode).url";\
     print "      )"
     print "  }";\
     next }1' ../app/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/utils/CheckYourAnswersHelper.scala

echo "Migration NonUkAddress completed"
