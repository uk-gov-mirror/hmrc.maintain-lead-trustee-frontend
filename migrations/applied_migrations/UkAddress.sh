#!/bin/bash

echo ""
echo "Applying migration UkAddress"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /ukAddress                        controllers.leadtrustee.individual.UkAddressController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /ukAddress                        controllers.leadtrustee.individual.UkAddressController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeUkAddress                  controllers.leadtrustee.individual.UkAddressController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeUkAddress                  controllers.leadtrustee.individual.UkAddressController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "ukAddress.title = ukAddress" >> ../conf/messages.en
echo "ukAddress.heading = ukAddress" >> ../conf/messages.en
echo "ukAddress.line1 = line1" >> ../conf/messages.en
echo "ukAddress.line2 = line2" >> ../conf/messages.en
echo "ukAddress.checkYourAnswersLabel = ukAddress" >> ../conf/messages.en
echo "ukAddress.error.line1.required = Enter line1" >> ../conf/messages.en
echo "ukAddress.error.line2.required = Enter line2" >> ../conf/messages.en
echo "ukAddress.error.line1.length = line1 must be line2 characters or less" >> ../conf/messages.en
echo "ukAddress.error.line2.length = line2 must be 100 characters or less" >> ../conf/messages.en

echo "Adding to UserAnswersEntryGenerators"
awk '/trait UserAnswersEntryGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryUkAddressUserAnswersEntry: Arbitrary[(UkAddressPage.type, JsValue)] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        page  <- arbitrary[UkAddressPage.type]";\
    print "        value <- arbitrary[UkAddress].map(Json.toJson(_))";\
    print "      } yield (page, value)";\
    print "    }";\
    next }1' ../test/generators/UserAnswersEntryGenerators.scala > tmp && mv tmp ../test/generators/UserAnswersEntryGenerators.scala

echo "Adding to PageGenerators"
awk '/trait PageGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryUkAddressPage: Arbitrary[UkAddressPage.type] =";\
    print "    Arbitrary(UkAddressPage)";\
    next }1' ../test/generators/PageGenerators.scala > tmp && mv tmp ../test/generators/PageGenerators.scala

echo "Adding to ModelGenerators"
awk '/trait ModelGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryUkAddress: Arbitrary[UkAddress] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        line1 <- arbitrary[String]";\
    print "        line2 <- arbitrary[String]";\
    print "      } yield UkAddress(line1, line2)";\
    print "    }";\
    next }1' ../test/generators/ModelGenerators.scala > tmp && mv tmp ../test/generators/ModelGenerators.scala

echo "Adding to UserAnswersGenerator"
awk '/val generators/ {\
    print;\
    print "    arbitrary[(UkAddressPage.type, JsValue)] ::";\
    next }1' ../test/generators/UserAnswersGenerator.scala > tmp && mv tmp ../test/generators/UserAnswersGenerator.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class/ {\
     print;\
     print "";\
     print "  def ukAddress: Option[AnswerRow] = userAnswers.get(UkAddressPage) map {";\
     print "    x =>";\
     print "      AnswerRow(";\
     print "        HtmlFormat.escape(messages(\"ukAddress.checkYourAnswersLabel\")),";\
     print "        HtmlFormat.escape(s\"${x.line1} ${x.line2}\"),";\
     print "        routes.UkAddressController.onPageLoad(CheckMode).url";\
     print "      )"
     print "  }";\
     next }1' ../app/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/utils/CheckYourAnswersHelper.scala

echo "Migration UkAddress completed"
