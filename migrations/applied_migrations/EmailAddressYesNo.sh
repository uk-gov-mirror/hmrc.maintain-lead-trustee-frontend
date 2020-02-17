#!/bin/bash

echo ""
echo "Applying migration EmailAddressYesNo"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /emailAddressYesNo                        controllers.leadtrustee.individual.EmailAddressYesNoController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /emailAddressYesNo                        controllers.leadtrustee.individual.EmailAddressYesNoController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeEmailAddressYesNo                  controllers.leadtrustee.individual.EmailAddressYesNoController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeEmailAddressYesNo                  controllers.leadtrustee.individual.EmailAddressYesNoController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "emailAddressYesNo.title = emailAddressYesNo" >> ../conf/messages.en
echo "emailAddressYesNo.heading = emailAddressYesNo" >> ../conf/messages.en
echo "emailAddressYesNo.checkYourAnswersLabel = emailAddressYesNo" >> ../conf/messages.en
echo "emailAddressYesNo.error.required = Select yes if emailAddressYesNo" >> ../conf/messages.en

echo "Adding to UserAnswersEntryGenerators"
awk '/trait UserAnswersEntryGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryEmailAddressYesNoUserAnswersEntry: Arbitrary[(EmailAddressYesNoPage.type, JsValue)] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        page  <- arbitrary[EmailAddressYesNoPage.type]";\
    print "        value <- arbitrary[Boolean].map(Json.toJson(_))";\
    print "      } yield (page, value)";\
    print "    }";\
    next }1' ../test/generators/UserAnswersEntryGenerators.scala > tmp && mv tmp ../test/generators/UserAnswersEntryGenerators.scala

echo "Adding to PageGenerators"
awk '/trait PageGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryEmailAddressYesNoPage: Arbitrary[EmailAddressYesNoPage.type] =";\
    print "    Arbitrary(EmailAddressYesNoPage)";\
    next }1' ../test/generators/PageGenerators.scala > tmp && mv tmp ../test/generators/PageGenerators.scala

echo "Adding to UserAnswersGenerator"
awk '/val generators/ {\
    print;\
    print "    arbitrary[(EmailAddressYesNoPage.type, JsValue)] ::";\
    next }1' ../test/generators/UserAnswersGenerator.scala > tmp && mv tmp ../test/generators/UserAnswersGenerator.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class/ {\
     print;\
     print "";\
     print "  def emailAddressYesNo: Option[AnswerRow] = userAnswers.get(EmailAddressYesNoPage) map {";\
     print "    x =>";\
     print "      AnswerRow(";\
     print "        HtmlFormat.escape(messages(\"emailAddressYesNo.checkYourAnswersLabel\")),";\
     print "        yesOrNo(x),";\
     print "        routes.EmailAddressYesNoController.onPageLoad(CheckMode).url";\
     print "      )"
     print "  }";\
     next }1' ../app/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/utils/CheckYourAnswersHelper.scala

echo "Migration EmailAddressYesNo completed"
