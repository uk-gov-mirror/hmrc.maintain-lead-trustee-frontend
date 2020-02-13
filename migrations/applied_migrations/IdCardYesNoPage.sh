#!/bin/bash

echo ""
echo "Applying migration IdCardYesNoPage"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /idCardYesNoPage                        controllers.leadtrustee.individual.IdCardYesNoPageController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /idCardYesNoPage                        controllers.leadtrustee.individual.IdCardYesNoPageController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeIdCardYesNoPage                  controllers.leadtrustee.individual.IdCardYesNoPageController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeIdCardYesNoPage                  controllers.leadtrustee.individual.IdCardYesNoPageController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "idCardYesNoPage.title = idCardYesNoPage" >> ../conf/messages.en
echo "idCardYesNoPage.heading = idCardYesNoPage" >> ../conf/messages.en
echo "idCardYesNoPage.checkYourAnswersLabel = idCardYesNoPage" >> ../conf/messages.en
echo "idCardYesNoPage.error.required = Select yes if idCardYesNoPage" >> ../conf/messages.en

echo "Adding to UserAnswersEntryGenerators"
awk '/trait UserAnswersEntryGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryIdCardYesNoPageUserAnswersEntry: Arbitrary[(IdCardYesNoPagePage.type, JsValue)] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        page  <- arbitrary[IdCardYesNoPagePage.type]";\
    print "        value <- arbitrary[Boolean].map(Json.toJson(_))";\
    print "      } yield (page, value)";\
    print "    }";\
    next }1' ../test/generators/UserAnswersEntryGenerators.scala > tmp && mv tmp ../test/generators/UserAnswersEntryGenerators.scala

echo "Adding to PageGenerators"
awk '/trait PageGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryIdCardYesNoPagePage: Arbitrary[IdCardYesNoPagePage.type] =";\
    print "    Arbitrary(IdCardYesNoPagePage)";\
    next }1' ../test/generators/PageGenerators.scala > tmp && mv tmp ../test/generators/PageGenerators.scala

echo "Adding to UserAnswersGenerator"
awk '/val generators/ {\
    print;\
    print "    arbitrary[(IdCardYesNoPagePage.type, JsValue)] ::";\
    next }1' ../test/generators/UserAnswersGenerator.scala > tmp && mv tmp ../test/generators/UserAnswersGenerator.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class/ {\
     print;\
     print "";\
     print "  def idCardYesNoPage: Option[AnswerRow] = userAnswers.get(IdCardYesNoPagePage) map {";\
     print "    x =>";\
     print "      AnswerRow(";\
     print "        HtmlFormat.escape(messages(\"idCardYesNoPage.checkYourAnswersLabel\")),";\
     print "        yesOrNo(x),";\
     print "        routes.IdCardYesNoPageController.onPageLoad(CheckMode).url";\
     print "      )"
     print "  }";\
     next }1' ../app/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/utils/CheckYourAnswersHelper.scala

echo "Migration IdCardYesNoPage completed"
