package com.demoblaze;

import io.cucumber.java.en.*;
import java.time.Duration;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;

public class MyStepDefinitions {
    WebDriver driver;
    WebDriverWait wait;

    @Given("user opens site")
    public void user_opens_site() {
        // تهيئة المتصفح والانتظار الذكي (Explicit Wait)
        driver = new ChromeDriver();
        wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        driver.manage().window().maximize();
        driver.get("https://www.demoblaze.com/");
    }

    @When("user clicks on signup with {string} and {string}")
    public void user_clicks_on_signup_with(String username, String password) {
        wait.until(ExpectedConditions.elementToBeClickable(By.id("signin2"))).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("sign-username"))).sendKeys(username);
        driver.findElement(By.id("sign-password")).sendKeys(password);
        driver.findElement(By.xpath("//button[text()='Sign up']")).click();
    }

    @Then("signup should be successful")
    public void signup_should_be_successful() {
        // التعامل مع رسالة التنبيه (Alert) بعد التسجيل
        wait.until(ExpectedConditions.alertIsPresent());
        Alert alert = driver.switchTo().alert();
        String alertText = alert.getText();
        alert.accept();
        // التحقق من النجاح أو أن المستخدم موجود مسبقاً (ص 2 من الكتيب)
        Assert.assertTrue(alertText.contains("successful") || alertText.contains("already exist"));
        driver.quit();
    }

    @When("user logins with {string} and {string}")
    public void user_logins_with(String user, String pass) {
        wait.until(ExpectedConditions.elementToBeClickable(By.id("login2"))).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("loginusername"))).sendKeys(user);
        driver.findElement(By.id("loginpassword")).sendKeys(pass);
        driver.findElement(By.xpath("//button[text()='Log in']")).click();
        
        // حل مشكلة الـ Timeout: تحديث الصفحة لضمان ظهور "Welcome"
        driver.navigate().refresh(); 
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("nameofuser")));
    }

    @And("user adds products and proceeds to checkout")
    public void user_adds_products_and_proceeds_to_checkout() {
        // تنفيذ السيناريو الثاني (ص 1 من الكتيب): إضافة منتجين
        
        // المنتج الأول: MacBook air
        addProductToCart("Laptops", "MacBook air");
        
        // العودة للرئيسية لإضافة المنتج الثاني
        driver.get("https://www.demoblaze.com/");
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("nava")));
        
        // المنتج الثاني: Sony vaio i5
        addProductToCart("Laptops", "Sony vaio i5");

        // الذهاب للسلة وإتمام الطلب (ص 2 و 3 من الكتيب)
        driver.get("https://www.demoblaze.com/cart.html");
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[text()='Place Order']"))).click();
        
        // ملء بيانات نموذج الشراء بالكامل (مطلوب ص 3)
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("name"))).sendKeys("Hemah Graduation");
        driver.findElement(By.id("country")).sendKeys("Egypt");
        driver.findElement(By.id("city")).sendKeys("Cairo");
        driver.findElement(By.id("card")).sendKeys("123456789012");
        driver.findElement(By.id("month")).sendKeys("12");
        driver.findElement(By.id("year")).sendKeys("2026");
        driver.findElement(By.xpath("//button[text()='Purchase']")).click();
    }

    // ميثود مساعدة (Reusable Method) تتبع مبدأ Clean Code
    private void addProductToCart(String category, String productName) {
        // اختيار الفئة
        wait.until(ExpectedConditions.elementToBeClickable(By.linkText(category))).click();
        
        // حل مشكلة StaleElementReferenceException بإعادة المحاولة عند الحاجة
        try {
            wait.until(ExpectedConditions.elementToBeClickable(By.linkText(productName))).click();
        } catch (StaleElementReferenceException e) {
            wait.until(ExpectedConditions.elementToBeClickable(By.linkText(productName))).click();
        }
        
        // الضغط على Add to cart
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//a[contains(@onclick,'addToCart')]"))).click();
        
        // قبول التنبيه بعد الإضافة
        wait.until(ExpectedConditions.alertIsPresent()).accept();
    }

    @Then("purchase should be completed successfully")
    public void purchase_should_be_completed_successfully() {
        // التحقق من ظهور رسالة النجاح النهائية (ص 3 من الكتيب)
        WebElement successMsg = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//h2[text()='Thank you for your purchase!']")));
        Assert.assertTrue(successMsg.isDisplayed());
        
        System.out.println("✅ تم إنهاء الاختبار بنجاح وجميع المتطلبات مطابقة للكتيب.");
        driver.quit();
    }
}