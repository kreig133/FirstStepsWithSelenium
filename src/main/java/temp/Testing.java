package temp;

import com.thoughtworks.selenium.Selenium;
import org.openqa.selenium.*;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.util.List;

public class Testing {
    public static void main(String[] args) throws InterruptedException {

        DesiredCapabilities ieCapabilities = DesiredCapabilities.internetExplorer();
        ieCapabilities.setCapability(InternetExplorerDriver.INTRODUCE_FLAKINESS_BY_IGNORING_SECURITY_DOMAINS, true);
        WebDriver driver = new InternetExplorerDriver(ieCapabilities);

        Selenium selenium = new WebDriverBackedSelenium(driver, args[0]);

        selenium.setTimeout( "60000" );
        selenium.open( args[0] );

        waitLoading( driver, 1 );

        authorization( args[ 1 ], driver );

        waitLoading( driver, 2 );

        workWithMenu( driver, new String[] { "Договор", "Список доступных договоров (Ctrl+Alt+O)" } );

        waitLoading( driver, 1 );

        //создаем новый фильтр
        WebElement element = driver.findElement( By.xpath( "//div[contains(text(), 'новый')][1]" ) );
        new Actions( driver ).doubleClick( element ).build().perform();

        waitLoading( driver, 1 );
        try{
            // Закрываем окно с предупреждением
            Alert alert = driver.switchTo().alert();
            alert.accept();
        } catch ( Exception e ) {}

        // Нажимаем на таб "Дополнительные"
        element = driver.findElement( By.xpath( "//span[@class='x-tab-strip-text  ' and text()='Дополнительные']" ) );
        element.click();

        waitLoading( driver, 2 );

        // Жмакаем на справочник
        element = driver.findElement( By.xpath( "//button[@class='GLSHMSKDOG']" ) );
        element.click();

        waitLoading( driver, 2 );

        // Выбираем чек-бокс "Проектное финансирование (ЮЛ)
        element = driver.findElement( By.xpath( "//div[text()='Проектное финансирование (ЮЛ)']/." +
                "./preceding-sibling::node()") );
        element.click();

        // Нажимаем на кнопку "Открыть"
        element = driver.findElement( By.xpath( "//button[@class='x-btn-text' and text() = 'Открыть']" ) );
        element.click();
        
        waitLoading( driver, 1 );

        // Применяем фильтр
        element = driver.findElement( By.xpath( "//button[@class='x-btn-text ' and text() = 'Применить']" ) );
        element.click();

        waitLoading( driver, 1 );

        // Список договоров
        final List<WebElement> elements =
                driver.findElements( By.xpath( "//div[@class='x-grid3-body']/child::node()" ) );

        for ( int i = 0; i < elements.size(); i ++  ) {
            // Запоминаем номер договора в строке
            String packNumber = driver.findElement( By.xpath( "//div[@class='x-grid3-body']/child::node()[" + (i +1) +
                    "]//td[@class='x-grid3-col x-grid3-cell x-grid3-td-sPackTitle   x-treegrid-column']//span[2]"
            ) ).getText();

            // Вызываем контекстное меню
            new Actions( driver ).contextClick(
                    // FIXME Костыль, по идее можно пользоваться списком полученным до этого,
                    // но так почему-то не заработало.
                    // Разбираться лень
                    driver.findElement( By.xpath( "//div[@class='x-grid3-body']/child::node()["+(i+1)+"]" ) )
            ).build().perform();

            // Выбираем пункт меню
            element = driver.findElement( By.xpath( "//a[text() = 'Треб. банка о доср. погашении']" ) );

            element.click();

            waitLoading( driver, 2 );

            // Находим номер договора в окне "Требования банка о досрочном погашении"
            element = driver.findElement( By.xpath(
                    "//div[contains( @class, 'gwt-Label' ) and text()='Договор :']/../following-sibling::node()/div"
            ) );

            System.out.println( "packNumberInTable  = " + packNumber );
            System.out.println( "packNumberInWindow = " + element.getText() );
            System.out.println( "Are they equal? " + packNumber.equals( element.getText() ) );

            // Закрываем окно
            element = driver.findElement( By.xpath( "//button[@class = 'x-btn-text ' and text()= 'Выход' ]" ) );
            element.click();

            waitLoading( driver, 1 );
        }


        driver.close();
    }

    private static void workWithMenu( WebDriver driver, String[] path ) {
        final String xpathExpression = "//td[@class='gwt-MenuItem']/span[text()='%s']";
        for ( String s : path ) {
            WebElement element = driver.findElement( By.xpath( String.format( xpathExpression, s ) ) );
            if ( s.equals( path[ path.length - 1 ] ) ) {
                element.click();
            } else {
                ( new Actions( driver ) ).moveToElement( element ).build().perform();
            }
        }
    }

    private static void waitLoading( WebDriver driver, int secondsForWait ) throws InterruptedException {
        int i = 0;
        while ( true ){
            boolean find = false;
            try{
                driver.findElement( By.xpath( "//span[text()='Подождите, Ваш запрос выполняется']" ) );
                find = true;
            } catch( Exception e){
            }
            try {
                driver.findElement( By.xpath(
                        "//div[@display='block']/img[@src='resources/images/default/shared/large-loading.gif']" ));
                find = true;
            } catch ( Exception e ) { }

            if( ! find ){
                i++;
            } else{
                i = 0;
            }
            if( i == secondsForWait ){
                break;
            }
            Thread.sleep( 1000L );
        }
    }

    private static void authorization( String login, WebDriver driver ) {

        WebElement element = driver.findElement( By.xpath( "//input[1]" ) );

        element.clear();

        element.sendKeys(login);

        element = driver.findElement( By.className( "gwt-PasswordTextBox" ) );

        element.sendKeys("123456");

        element = driver.findElement( By.xpath( "//button[1]" ) );
        element.click();
    }
}
