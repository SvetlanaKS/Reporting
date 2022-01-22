package ru.netology.delivery.test;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Selectors;
import com.codeborne.selenide.logevents.SelenideLogger;
import org.junit.jupiter.api.*;
import org.openqa.selenium.Keys;
import ru.netology.delivery.data.DataGenerator;
import io.qameta.allure.selenide.AllureSelenide;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import java.time.Duration;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.*;

class DeliveryTest {
    @BeforeAll
    static void setUpAll () {
        SelenideLogger.addListener("allure",new AllureSelenide());
    }

    @AfterAll
    static void tearDownAll (){
        SelenideLogger.removeListener("allure");
    }
    @BeforeEach
    void setup() {
        Configuration.browser = "chrome";
        open("http://localhost:9999");
    }

    @Test
  @DisplayName("Should successful plan and  new plan meeting")
        //Заполение формы
    void shouldOrderCardDelivery() {
        int daysToAddForFirstMeeting = 4;
        String formatDate = DataGenerator.dateOfMeeting(daysToAddForFirstMeeting, "dd.MM.yyyy");

        $("[data-test-id=city] .input__control").setValue(DataGenerator.generateCity("ru"));
        $("[data-test-id=date] .input__control").sendKeys(Keys.CONTROL + "A");
        $("[data-test-id=date] .input__control").sendKeys(Keys.BACK_SPACE);
        $("[data-test-id=date] .input__control").setValue(formatDate);
        $("[data-test-id=name] .input__control").setValue(DataGenerator.generateName("ru"));
        $("[data-test-id=phone] .input__control").setValue(DataGenerator.generatePhone("ru"));

        $("div form fieldset label").click();
        $$(".button").findBy(Condition.exactText("Запланировать")).click();

        // Всплывающее окно успешно
        $(Selectors.withText("Успешно")).shouldBe(visible, Duration.ofSeconds(15));

        // Окно "Успешно" имеет текст "Встреча успешно запланирована на + дата"
        $("div.notification__content").shouldHave(Condition.exactText("Встреча успешно запланирована на " + formatDate));

        //Перепланируем встречу
        int daysToAddForSecondMeeting = 7;
        String newFormatDate = DataGenerator.dateOfMeeting(daysToAddForSecondMeeting, "dd.MM.yyyy");
        $("[data-test-id=date] .input__control").sendKeys(Keys.CONTROL + "A");
        $("[data-test-id=date] .input__control").sendKeys(Keys.BACK_SPACE);
        $("[data-test-id=date] .input__control").setValue(newFormatDate);
        $$(".button").findBy(Condition.exactText("Запланировать")).click();

        // Всплывающее окно "Необходимо подтверждение"
        $(Selectors.withText("Необходимо подтверждение")).shouldBe(visible);
        $(Selectors.withText("У вас уже запланирована встреча на другую дату. Перепланировать?")).shouldBe(visible);

        // Нажать "Перепланировать"
        $(Selectors.byText("Перепланировать")).click();

        // Всплывающее окно успешно
        $(Selectors.withText("Успешно")).shouldBe(visible, Duration.ofSeconds(15));

        // Окно "Успешно" имеет текст "Встреча успешно запланирована на + дата"
        $("div.notification__content").shouldHave(Condition.exactText("Встреча успешно запланирована на " + newFormatDate));

    }
}