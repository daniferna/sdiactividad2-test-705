package uniovi.tests;

import org.junit.*;
import org.junit.runners.MethodSorters;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import uniovi.tests.pageobjects.*;
import uniovi.utils.SeleniumUtils;

import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

// Se ordenan las pruebas por el nombre del metodo
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class WallapopTest {

    static String URLlocal = "http://localhost:8081";
    static String URLremota = "ec2-35-180-86-141.eu-west-3.compute.amazonaws.com:8080";
    static String URL = URLlocal; // Se va a probar con la URL remota, sino URL=URLlocal

    //En Windows (Debe ser la versión 65.0.1 y desactivar las actualizacioens automáticas)):
    static String PathFirefox65 = "C:\\Program Files\\Mozilla Firefox\\firefox.exe";
    static String Geckdriver024 = "C:\\Users\\Daniel\\OneDrive - Universidad de Oviedo" +
            "\\Tercer Curso\\SDI\\Practicas\\Spring Boot\\PL-SDI-Sesión5-material\\PL-SDI-Sesión5-material\\geckodriver024win64.exe";
    //Común a Windows y a MACOSX
    static WebDriver driver = getDriver(PathFirefox65, Geckdriver024);

    public static WebDriver getDriver(String PathFirefox, String Geckdriver) {
        System.setProperty("webdriver.firefox.bin", PathFirefox);
        System.setProperty("webdriver.gecko.driver", Geckdriver);
        WebDriver driver = new FirefoxDriver();
        return driver;
    }

    //Después de cada prueba se borran las cookies del navegador
    @After
    public void tearDown() {
        driver.manage().deleteAllCookies();
    }

    //Antes de la primera prueba
    @BeforeClass
    static public void begin() {
        driver.navigate().to(URL + "/api/resetDatabase");
    }

    @Before
    public void setUp() {
        driver.navigate().to(URL);
    }

    //Al finalizar la última prueba
    @AfterClass
    static public void end() {
        //Cerramos el navegador al finalizar las pruebas
        driver.quit();
    }

    //PR01. Prueba del formulario de registro. registro con datos correctos
    @Test
    public void PR01() {
        //Vamos al formulario de registro
        PO_HomeView.clickOption(driver, "signup", "class", "btn btn-primary");
        //Rellenamos el formulario.
        PO_RegisterView.fillForm(driver, "auser@mail.com", "Josefo", "Perez", "77777",
                "77777");
        //Comprobamos que entramos en la sección privada
        PO_View.checkElement(driver, "text", "Ofertas del usuario");
    }

    //PR06. Registro de Usuario con datos inválidos (email vacío, nombre vacío, apellidos vacíos).
    @Test
    public void PR02() {
        //Vamos al formulario de registro
        PO_HomeView.clickOption(driver, "signup", "class", "btn btn-primary");
        //Email vacio
        PO_RegisterView.fillForm(driver, " ", "Josefo", "Perez", "77777",
                "77777");
        PO_View.getP();
        //Comprobamos el error de campo vacio
        PO_RegisterView.checkKey(driver, "El campo e-mail no puede estar vacio",
                PO_Properties.getSPANISH());
        //Vamos al formulario de registro
        PO_HomeView.clickOption(driver, "signup", "class", "btn btn-primary");
        //Contraseña mal repetida
        PO_RegisterView.fillForm(driver, "email@email.com", "Josefo", "Perez", "77577",
                "77777");
        PO_View.getP();
        //Comprobamos el error de contraseña
        PO_RegisterView.checkKey(driver, "Las contraseñas no coinciden",
                PO_Properties.getSPANISH());
    }

    //PR03. Registro de Usuario con datos inválidos (email existente)
    @Test
    public void PR03() {
        //Vamos al formulario de registro
        PO_HomeView.clickOption(driver, "signup", "class", "btn btn-primary");
        //Rellenamos el formulario.
        PO_RegisterView.fillForm(driver, "auser@mail.com", "Josefo", "Perez", "77777",
                "77777");
        //Comprobamos el error de e-mail duplicado
        PO_RegisterView.checkKey(driver, "Ya existe un usuario con el E-Mail introducido",
                PO_Properties.getSPANISH());
    }

    //PR04. Inicio de sesion con datos validos
    @Test
    public void PR04() {

        PO_LoginView.fillForm(driver, "admin@email.com", "admin");

        PO_NavView.checkElement(driver, "id", "tableUsers");
    }

    //PR05: Identificación datos invalidos (email existente pero contraseña incorrecta)
    @Test
    public void PR05() {

        PO_LoginView.fillForm(driver, "admin@email.com", "fallo");

        PO_RegisterView.checkKey(driver, "Email o contraseña incorrectos",
                PO_Properties.getSPANISH());
    }

    //PR06. Inicio de sesion con datos validos
    @Test
    public void PR06() {

        // Busca el campo de email que ha sido marcado como necesario
        WebElement elem1 = driver.findElement(By.cssSelector("input:required"));

        PO_LoginView.fillForm(driver, "", "admin");

        if (elem1 == null)
            fail();

        // Busca el cuadro de HTML5 de campo requerido
        WebElement elem2 = driver.findElement(By.cssSelector("input:invalid"));

        if (elem2 == null)
            fail();

        setUp();

        PO_LoginView.fillForm(driver, "admin@email.com", "");

        // Busca el cuadro de HTML5 de campo requerido
        WebElement elem3 = driver.findElement(By.cssSelector("input:invalid"));

        if (elem3 == null)
            fail();
    }

    //PR07: Inicio de sesión con datos inválidos (email no existente).
    @Test
    public void PR07() {
        PO_LoginView.fillForm(driver, "noexisto@email.com", "hola");

        PO_RegisterView.checkKey(driver, "Email o contraseña incorrectos",
                PO_Properties.getSPANISH());
    }

    //PR08: Hacer click en la opción de salir de sesión y comprobar que se redirige a la página de inicio
    //      de sesión (Login).
    @Test
    public void PR08() {
        //Rellenamos el formulario
        PO_LoginView.fillForm(driver, "admin@email.com", "admin");
        //Nos desconectamos
        PO_View.checkElement(driver, "id", "userOptionsButton").get(0).click();
        PO_View.checkElement(driver, "id", "logoutLink").get(0).click();
        //Comprobamos que estamos en la pagina de login mirando a ver si esta el boton de login
        PO_LoginView.checkElement(driver, "id", "btn_login");
    }

    //PR09: Comprobar que el botón cerrar sesión no está visible si el usuario no está autenticado
    @Test
    public void PR09() {
        if (driver.findElements(By.xpath("//*[contains(@id,'logoutLink')]")).size() != 0)
            fail();
    }

    //PR10. Mostrar el listado de usuarios y comprobar que se muestran todos los que existen en el
    //      sistema.
    @Test
    public void PR10() {
        //Rellenamos el formulario
        PO_LoginView.fillForm(driver, "admin@email.com", "admin");
        //Esperamos a que se muestre el boton de eliminar usuarios
        PO_View.checkElement(driver, "id", "removeUsersButton");
        //Comprobamos que estan todos los usuarios
        PO_View.checkElement(driver, "text", "danixe.ferna@gmail.com");
        PO_View.checkElement(driver, "text", "user1@email.com");
        PO_View.checkElement(driver, "text", "auser@mail.com");
    }

    //PR11: Ir a la lista de usuarios, borrar el primer usuario de la lista, comprobar que la lista se actualiza
    //      y dicho usuario desaparece.
    @Test
    public void PR11() {
        //Rellenamos el formulario
        PO_LoginView.fillForm(driver, "admin@email.com", "admin");
        //Esperamos a que se muestre el boton de eliminar usuarios
        PO_View.checkElement(driver, "id", "removeUsersButton");
        //Eliminamos el primer usuario
        PO_View.checkElement(driver, "id", "removeUserButton-auser@mail.com").get(0).click();
        //Comprobamos que estan todos
        PO_View.checkElement(driver, "text", "danixe.ferna@gmail.com");
        PO_View.checkElement(driver, "text", "user1@email.com");
        //Y el no
        assertTrue(driver.findElements(By.xpath("//*[contains(text(),'auser@email.com')]")).isEmpty());
    }

    //PR12: Ir a la lista de usuarios, borrar el último usuario de la lista, comprobar que la lista se actualiza
    //y dicho usuario desaparece.
    @Test
    public void PR12() {
        // PRIMERO AGREGAMOS EL USUARIO A BORRAR
        PO_View.agregarUsuario(driver, "zuser@email.com", "Usuario Z", "Zacarias");

        //Rellenamos el formulario
        PO_LoginView.fillForm(driver, "admin@email.com", "admin");
        //Esperamos a que se muestre el boton de eliminar usuarios
        PO_View.checkElement(driver, "id", "removeUsersButton");
        //Eliminamos el primer usuario
        PO_View.checkElement(driver, "id", "removeUserButton-zuser@email.com").get(0).click();
        //Comprobamos que estan todos
        PO_View.checkElement(driver, "text", "danixe.ferna@gmail.com");
        PO_View.checkElement(driver, "text", "user1@email.com");
        //Y el no
        assertTrue(driver.findElements(By.xpath("//*[contains(text(),'zuser@email.com')]")).isEmpty());
    }

    //PR13: Ir a la lista de usuarios, borrar 3 usuarios, comprobar que la lista se actualiza y dichos
    //usuarios desaparecen.
    @Test
    public void PR13() {
        //PRIMERO LOS AGREGAMOS
        PO_View.agregarUsuario(driver, "user1Test@mail.com", "Test1", "Numero 1");
        PO_View.agregarUsuario(driver, "user2Test@mail.com", "Test2", "Numero 2");
        PO_View.agregarUsuario(driver, "user3Test@mail.com", "Test3", "Numero 3");

        PO_LoginView.fillForm(driver, "admin@email.com", "admin");
        //Esperamos a que se muestre el boton de eliminar usuarios
        PO_View.checkElement(driver, "id", "removeUsersButton");
        //Clicamos las casillas
        PO_View.checkElement(driver, "id", "chkUserID-user1Test@mail.com").get(0).click();
        PO_View.checkElement(driver, "id", "chkUserID-user2Test@mail.com").get(0).click();
        PO_View.checkElement(driver, "id", "chkUserID-user3Test@mail.com").get(0).click();
        //Eliminamos
        PO_View.checkElement(driver, "id", "removeUsersButton").get(0).click();
        //Comprobamos que estan todos
        PO_View.checkElement(driver, "id", "removeUsersButton");
        PO_View.checkElement(driver, "text", "danixe.ferna@gmail.com");
        PO_View.checkElement(driver, "text", "user1@email.com");
        //Y ellos no
        assertTrue(driver.findElements(By.xpath("//*[contains(text(),'user1Test@email.com')]")).isEmpty());
        assertTrue(driver.findElements(By.xpath("//*[contains(text(),'user2Test@email.com')]")).isEmpty());
        assertTrue(driver.findElements(By.xpath("//*[contains(text(),'user3Test@email.com')]")).isEmpty());
    }

    //PR14: Ir al formulario de alta de oferta, rellenarla con datos válidos y pulsar el botón Submit.
    //Comprobar que la oferta sale en el listado de ofertas de dicho usuario.
    @Test
    public void PR14() {
        //Rellenamos el formulario
        PO_LoginView.fillForm(driver, "user1@email.com", "user1");
        //Pinchamos en la opción de menu de Ofertas:
        PO_View.checkElement(driver, "id", "offersManage").get(0).click();
        //Esperamos a aparezca la opción de añadir oferta
        PO_View.checkElement(driver, "id", "addOfferLink").get(0).click();
        //Ahora vamos a rellenar la oferta
        PO_PrivateView.fillFormAddOffer(driver, "Oferta nueva", "Oferta de prueba", 15);
        //Vamos a home
        driver.navigate().to(URL + "/");
        //Comprobamos que sale la oferta nueva
        PO_View.checkElement(driver, "text", "Oferta nueva");
    }

    //PR15: Ir al formulario de alta de oferta, rellenarla con datos inválidos (campo título vacío) y pulsar
    //el botón Submit. Comprobar que se muestra el mensaje de campo obligatorio.
    @Test
    public void PR15() {
        //Rellenamos el formulario
        PO_LoginView.fillForm(driver, "user1@email.com", "user1");
        //Pinchamos en la opción de menu de Ofertas:
        PO_View.checkElement(driver, "id", "offersManage").get(0).click();
        //Esperamos a aparezca la opción de añadir oferta
        PO_View.checkElement(driver, "id", "addOfferLink").get(0).click();
        //Ahora vamos a rellenar la oferta
        PO_PrivateView.fillFormAddOffer(driver, "", "Oferta de prueba", 15);
        // Busca el cuadro de HTML5 de campo requerido
        SeleniumUtils.esperarSegundos(driver, 1);
        WebElement elem3 = driver.findElement(By.cssSelector("input:invalid"));
        if (elem3 == null)
            fail();
    }

    //PR16: Mostrar el listado de ofertas para dicho usuario y comprobar que se muestran todas los que
    //existen para este usuario.
    @Test
    public void PR16() {
        //Rellenamos el formulario
        PO_LoginView.fillForm(driver, "user1@email.com", "user1");
        //Comprobamos que se listan todas las ofertas del usuario
        PO_HomeView.checkElement(driver, "text", "Oferta nueva");
        PO_HomeView.checkElement(driver, "text", "TV");
    }

    //PR17: Ir a la lista de ofertas, borrar la primera oferta de la lista, comprobar que la lista se actualiza y
    //que la oferta desaparece.
    @Test
    public void PR17() {
        ///Rellenamos el formulario
        PO_LoginView.fillForm(driver, "user1@email.com", "user1");
        //Borramos la primera oferta
        PO_HomeView.checkElement(driver, "id", "offerRemoveButton-Oferta nueva").get(0).click();
        // Comprobamos que no esta ya
        assertTrue(driver.findElements(By.xpath("//*[contains(text(),'Oferta nueva')]")).isEmpty());
    }

    //PR18: Ir a la lista de ofertas, borrar la última oferta de la lista, comprobar que la lista se actualiza y
    //que la oferta desaparece.
    @Test
    public void PR18() {
        // Agregamos una oferta
        PO_LoginView.fillForm(driver, "user1@email.com", "user1");
        //Pinchamos en la opción de menu de Ofertas:
        PO_View.checkElement(driver, "id", "offersManage").get(0).click();
        //Esperamos a aparezca la opción de añadir oferta
        PO_View.checkElement(driver, "id", "addOfferLink").get(0).click();
        //Ahora vamos a rellenar la oferta
        PO_PrivateView.fillFormAddOffer(driver, "Xbox One", "Con juegos", 230);
        //Vamos a home
        driver.navigate().to(URL + "/");
        //Borramos la ultima oferta
        PO_HomeView.checkElement(driver, "id", "offerRemoveButton-Xbox One").get(0).click();
        // Comprobamos que no esta ya
        assertTrue(driver.findElements(By.xpath("//*[contains(text(),'Xbox One')]")).isEmpty());
    }

    //PR19: Hacer una búsqueda con el campo vacío y comprobar que se muestra la página que
    //corresponde con el listado de las ofertas existentes en el sistema
    @Test
    public void PR19() {
        //Rellenamos el formulario
        PO_LoginView.fillForm(driver, "admin@email.com", "admin");
        //Pinchamos en la opción de ver ofertas disponibles
        PO_View.checkElement(driver, "id", "offersManage").get(0).click();
        PO_View.checkElement(driver, "id", "seeAvailableOffersButton").get(0).click();
        //Esperamos a que aparezcan las ofertas
        PO_View.checkElement(driver, "id", "searchBox");
        // Buscamos con el cuadro vacio
        PO_View.checkElement(driver, "id", "searchButton").get(0).click();
        // Esperamos a que recarguen
        PO_View.checkElement(driver, "id", "searchBox");
        //Comprobamos que salen las que tienen que salir
        PO_HomeView.checkElement(driver, "text", "PS4");
        PO_HomeView.checkElement(driver, "text", "Televisor");
        PO_HomeView.checkElement(driver, "text", "Samsung Galaxy S6");
        PO_HomeView.checkElement(driver, "text", "PS2");
        PO_HomeView.checkElement(driver, "text", "Monitor 24\"");
    }

    //PR20: Ir a la lista de ofertas, borrar la última oferta de la lista, comprobar que la lista se actualiza y
    //que la oferta desaparece
    @Test
    public void PR20() {
        //Rellenamos el formulario
        PO_LoginView.fillForm(driver, "admin@email.com", "admin");
        //Pinchamos en la opción de ver ofertas disponibles
        PO_View.checkElement(driver, "id", "offersManage").get(0).click();
        PO_View.checkElement(driver, "id", "seeAvailableOffersButton").get(0).click();
        //Esperamos a que aparezcan las ofertas
        PO_View.checkElement(driver, "id", "searchBox").get(0).sendKeys("Cosa que no existe");
        // Buscamos
        PO_View.checkElement(driver, "id", "searchButton").get(0).click();
        // Esperamos a que recarguen
        PO_View.checkElement(driver, "id", "searchBox");
        //Comprobamos que no sale nainguna
        assertEquals(0, driver.findElements(By.id("sendMessageButton")).size());
    }

    //PR21: Hacer una búsqueda escribiendo en el campo un texto en minúscula o mayúscula y
    //comprobar que se muestra la página que corresponde, con la lista de ofertas que contengan dicho texto,
    //independientemente que el título esté almacenado en minúsculas o mayúscula
    @Test
    public void PR21() {
        //Rellenamos el formulario
        PO_LoginView.fillForm(driver, "admin@email.com", "admin");
        //Pinchamos en la opción de ver ofertas disponibles
        PO_View.checkElement(driver, "id", "offersManage").get(0).click();
        PO_View.checkElement(driver, "id", "seeAvailableOffersButton").get(0).click();
        //Esperamos a que aparezcan las ofertas
        PO_View.checkElement(driver, "id", "searchBox").get(0).sendKeys("pS");
        // Buscamos
        PO_View.checkElement(driver, "id", "searchButton").get(0).click();
        // Esperamos a que recarguen
        PO_View.checkElement(driver, "id", "searchBox");
        //Comprobamos que salen las que tienen que salir
        PO_HomeView.checkElement(driver, "text", "PS4");
        PO_HomeView.checkElement(driver, "text", "PS2");
    }

    //PR22: Sobre una búsqueda determinada (a elección de desarrollador), comprar una oferta que deja
    //un saldo positivo en el contador del comprobador. Y comprobar que el contador se actualiza
    //correctamente en la vista del comprador.
    @Test
    public void PR22() {
        //Rellenamos el formulario
        PO_LoginView.fillForm(driver, "admin@email.com", "admin");
        //Pinchamos en la opción de ver ofertas disponibles
        PO_View.checkElement(driver, "id", "offersManage").get(0).click();
        PO_View.checkElement(driver, "id", "seeAvailableOffersButton").get(0).click();
        //Esperamos a que aparezcan las ofertas
        PO_View.checkElement(driver, "id", "searchBox").get(0).sendKeys("PS4");
        // Buscamos
        PO_View.checkElement(driver, "id", "searchButton").get(0).click();
        // Esperamos a que recarguen
        PO_View.checkElement(driver, "id", "searchBox");
        //Compramos la PS4
        PO_HomeView.checkElement(driver, "id", "offerBuyButton-PS4").get(0).click();
        // Vemos cuanto saldo nos queda en la cuenta
        double dinero = Double.parseDouble(PO_View.checkElement(driver, "id", "moneyUserCounter")
                .get(0).getText().replace('€', ' '));
        assertEquals(15, dinero, 0.1);

        //AHORA LA DEVOLVEMOS
        PO_View.checkElement(driver, "id", "offersManage").get(0).click();
        PO_View.checkElement(driver, "id", "seeBoughtOffersButton").get(0).click();
        PO_View.checkElement(driver, "class", "btn btn-danger").get(0).click();
    }

    //PR23: Sobre una búsqueda determinada (a elección de desarrollador), comprar una oferta que deja
    //un saldo 0 en el contador del comprobador. Y comprobar que el contador se actualiza correctamente en
    //la vista del comprador.
    @Test
    public void PR23() {
        //Rellenamos el formulario
        PO_LoginView.fillForm(driver, "admin@email.com", "admin");
        //Compramos la PS2
        PO_View.comprar(driver, "PS2");
        // Vemos cuanto saldo nos queda en la cuenta
        double dinero = Double.parseDouble(PO_View.checkElement(driver, "id", "moneyUserCounter")
                .get(0).getText().replace('€', ' '));
        assertEquals(0, dinero, 0.1);

        //AHORA LA DEVOLVEMOS
        PO_View.checkElement(driver, "id", "offersManage").get(0).click();
        PO_View.checkElement(driver, "id", "seeBoughtOffersButton").get(0).click();
        PO_View.checkElement(driver, "class", "btn btn-danger").get(0).click();
    }

    //PR24: Sobre una búsqueda determinada (a elección de desarrollador), intentar comprar una oferta
    //que esté por encima de saldo disponible del comprador. Y comprobar que se muestra el mensaje de
    //saldo no suficiente.
    @Test
    public void PR24() {
        //Rellenamos el formulario
        PO_LoginView.fillForm(driver, "admin@email.com", "admin");
        //Pinchamos en la opción de ver ofertas disponibles
        PO_View.checkElement(driver, "id", "offersManage").get(0).click();
        PO_View.checkElement(driver, "id", "seeAvailableOffersButton").get(0).click();
        //Esperamos a que aparezcan las ofertas
        PO_View.checkElement(driver, "id", "searchBox").get(0).sendKeys("Televisor");
        // Buscamos la oferta que queramos
        PO_View.checkElement(driver, "id", "searchButton").get(0).click();
        // Esperamos a que recarguen
        PO_View.checkElement(driver, "id", "searchBox");
        // Comprobamos que aparece el aviso de que no tenemos dinero
        PO_View.checkElement(driver, "text", "Dinero");
        PO_View.checkElement(driver, "class", "btn btn-warning disabled");
    }

    //PR25: Ir a la opción de ofertas compradas del usuario y mostrar la lista. Comprobar que aparecen
    //las ofertas que deben aparecer.
    @Test
    public void PR25() {
        //Rellenamos el formulario
        PO_LoginView.fillForm(driver, "admin@email.com", "admin");
        //PRIMERO COMPRAMOS ALGUNA OFERTA
        PO_View.comprar(driver, "Samsung Galaxy S6");
        driver.navigate().to(URL);
        PO_View.comprar(driver, "Chupete gastado");
        driver.navigate().to(URL);
        // Vamos a la lista de ofertas compradas
        PO_View.checkElement(driver, "id", "offersManage").get(0).click();
        PO_View.checkElement(driver, "id", "seeBoughtOffersButton").get(0).click();
        // Comprobamos que aparecen
        PO_View.checkElement(driver, "text", "Samsung Galaxy S6");
        PO_View.checkElement(driver, "text", "Chupete gastado");
        // Las devolvemos
        PO_View.checkElement(driver, "class", "btn btn-danger").get(0).click();
        PO_View.checkElement(driver, "class", "btn btn-danger").get(0).click();
    }

    //PR29: Inicio de sesión con datos válidos
    @Test
    public void PR29() {
        // Accedemos a la web que funciona con la API y jQuery
        driver.navigate().to(URL + "/ClienteREST.html");
        // Logeamos
        PO_View.checkElement(driver, "id", "loginButton");
        PO_LoginView.fillFormREST(driver, "user1@email.com", "user1");
        // Comprobamos que hemos accedido
        PO_View.checkElement(driver, "text", "Ofertas disponibles");
        // Nos deslogeamos
        PO_View.checkElement(driver, "id", "logoutLink").get(0).click();
    }

    //PR30: Inicio de sesión con datos inválidos (email existente, pero contraseña incorrecta).
    @Test
    public void PR30() {
        // Accedemos a la web que funciona con la API y jQuery
        driver.navigate().to(URL + "/ClienteREST.html");
        // Logeamos
        PO_View.checkElement(driver, "id", "loginButton");
        PO_LoginView.fillFormREST(driver, "user1@email.com", "dsad");
        // Comprobamos que no hemos accedido
        PO_View.checkElement(driver, "text", "Usuario no encontrado");
    }

    //PR31: Inicio de sesión con datos válidos (campo email o contraseña vacíos).
    @Test
    public void PR31() {
        // Accedemos a la web que funciona con la API y jQuery
        driver.navigate().to(URL + "/ClienteREST.html");
        // Logeamos
        PO_View.checkElement(driver, "id", "loginButton");
        PO_LoginView.fillFormREST(driver, "user1@email.com", "");
        // Comprobamos que nos salta el aviso de campo vacio
        // Busca el cuadro de HTML5 de campo requerido
        WebElement elem3 = driver.findElement(By.cssSelector("input:invalid"));
        if (elem3 == null)
            fail();
    }

    //PR32: Mostrar el listado de ofertas disponibles y comprobar que se muestran todas las que existen,
    //menos las del usuario identificado.
    @Test
    public void PR32() {
        // Accedemos a la web que funciona con la API y jQuery
        driver.navigate().to(URL + "/ClienteREST.html");
        // Logeamos
        PO_View.checkElement(driver, "id", "loginButton");
        PO_LoginView.fillFormREST(driver, "user1@email.com", "user1");
        // Comprobamos que hemos accedido
        PO_View.checkElement(driver, "text", "Ofertas disponibles");
        // Comprobamos que estan las ofertas que deberian
        PO_HomeView.checkElement(driver, "text", "PS4");
        PO_HomeView.checkElement(driver, "text", "PS2");
        PO_HomeView.checkElement(driver, "text", "Set 10 platos llanos");
        PO_HomeView.checkElement(driver, "text", "Chupete gastado");
        PO_HomeView.checkElement(driver, "text", "Televisor");
        PO_HomeView.checkElement(driver, "text", "Samsung Galaxy S6");
        PO_HomeView.checkElement(driver, "text", "Monitor 24\"");
        // Nos deslogeamos
        PO_View.checkElement(driver, "id", "logoutLink").get(0).click();
    }

    //PR33:  Sobre una búsqueda determinada de ofertas (a elección de desarrollador), enviar un mensaje a
    //una oferta concreta. Se abriría dicha conversación por primera vez. Comprobar que el mensaje aparece
    //en el listado de mensajes.
    @Test
    public void PR33() {
        //Rellenamos el formulario
        PO_LoginView.fillForm(driver, "user1@email.com", "user1");
        //Pinchamos en la opción de ver ofertas disponibles
        PO_View.checkElement(driver, "id", "offersManage").get(0).click();
        PO_View.checkElement(driver, "id", "seeAvailableOffersButton").get(0).click();
        //Esperamos a que aparezcan las ofertas
        PO_View.checkElement(driver, "id", "searchBox").get(0).sendKeys("Televisor");
        // Buscamos la oferta que queremos
        PO_View.checkElement(driver, "id", "searchButton").get(0).click();
        // Esperamos a que recarguen
        PO_View.checkElement(driver, "id", "sendMessageButton-Televisor").get(0).click();
        // Escribimos el mensaje
        PO_View.checkElement(driver, "id", "textBox").get(0).sendKeys("Mensaje inicial de prueba");
        // Lo enviamos
        PO_View.checkElement(driver, "id", "sendButton").get(0).click();
        // Esperamos un par de segundos que recargue el chat
        SeleniumUtils.esperarSegundos(driver, 2);
        // Nos aseguramos que el campo de escribir esta vacio y vemos si esta el mensaje
        PO_View.checkElement(driver, "id", "textBox").get(0).clear();
        PO_View.checkElement(driver, "text", "Mensaje inicial de prueba");
    }

    //PR34: Sobre el listado de conversaciones enviar un mensaje a una conversación ya abierta.
    //Comprobar que el mensaje aparece en el listado de mensajes
    @Test
    public void PR34() {
        //Rellenamos el formulario
        PO_LoginView.fillForm(driver, "daniferna@outlook.com", "test");
        //Redirigimos a la lista de ofertas propias
        driver.navigate().to(URL);
        //Pinchamos en el boton de conversaciones del televisor
        PO_View.checkElement(driver, "id", "conversationsButton-Televisor").get(0).click();
        PO_View.checkElement(driver, "id", "conversationButton-user1@email.com").get(0).click();
        //Esperamos a que cargue el chat y escribimos
        PO_View.checkElement(driver, "id", "textBox").get(0).sendKeys("Segundo mensaje");
        // Lo enviamos
        PO_View.checkElement(driver, "id", "sendButton").get(0).click();
        // Esperamos un par de segundos que recargue el chat
        SeleniumUtils.esperarSegundos(driver, 2);
        // Nos aseguramos que el campo de escribir esta vacio y vemos si esta el mensaje
        PO_View.checkElement(driver, "id", "textBox").get(0).clear();
        PO_View.checkElement(driver, "text", "Segundo mensaje");
    }

    //PR35: Mostrar el listado de conversaciones ya abiertas. Comprobar que el listado contiene las
    //conversaciones que deben ser.
    @Test
    public void PR35() {
        //Creamos las conversaciones
        PO_View.crearConversacionPS4(driver, "user1@email.com", "user1");
        PO_View.crearConversacionPS4(driver, "danixe.ferna@gmail.com", "test");
        SeleniumUtils.esperarSegundos(driver, 1);
        //Rellenamos el formulario
        PO_LoginView.fillForm(driver, "daniferna@outlook.com", "test");
        //Redirigimos a la lista de ofertas propias
        driver.navigate().to(URL);
        //Pinchamos en el boton de conversaciones de la PS4
        PO_View.checkElement(driver, "id", "conversationsButton-PS4").get(0).click();
        //Comprobamos que se listan las conversaciones que deben listarse
        PO_View.checkElement(driver, "text", "user1@email.com").get(0).click();
        PO_View.checkElement(driver, "text", "danixe.ferna@gmail.com").get(0).click();
    }

    //PR36: Sobre el listado de conversaciones ya abiertas. Pinchar el enlace Eliminar de la primera y
    //comprobar que el listado se actualiza correctamente.
    @Test
    public void PR36() {
        //Rellenamos el formulario
        PO_LoginView.fillForm(driver, "daniferna@outlook.com", "test");
        //Redirigimos a la lista de ofertas propias
        driver.navigate().to(URL);
        //Pinchamos en el boton de conversaciones de la PS4
        PO_View.checkElement(driver, "id", "conversationsButton-PS4").get(0).click();
        //Borramos la primera y comprobamos que ya no esta
        PO_View.checkElement(driver, "id", "removeButton-user1@email.com").get(0).click();
        assertTrue(driver.findElements(By.xpath("//*[contains(text(),'user1@email.com')]")).isEmpty());
    }

    //PR37: Sobre el listado de conversaciones ya abiertas. Pinchar el enlace Eliminar de la última y
    //comprobar que el listado se actualiza correctamente.
    @Test
    public void PR37() {
        //Creamos otra conversacion
        PO_View.crearConversacionPS4(driver, "user1@email.com", "user1");
        SeleniumUtils.esperarSegundos(driver, 1);
        //Rellenamos el formulario
        PO_LoginView.fillForm(driver, "daniferna@outlook.com", "test");
        //Redirigimos a la lista de ofertas propias
        driver.navigate().to(URL);
        //Pinchamos en el boton de conversaciones de la PS4
        PO_View.checkElement(driver, "id", "conversationsButton-PS4").get(0).click();
        //Borramos la primera y comprobamos que ya no esta
        PO_View.checkElement(driver, "id", "removeButton-danixe.ferna@gmail.com").get(0).click();
        assertTrue(driver.findElements(By.xpath("//*[contains(text(),'danixe.ferna@gmail.com')]")).isEmpty());
    }


}