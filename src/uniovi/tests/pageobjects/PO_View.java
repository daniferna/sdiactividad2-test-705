package uniovi.tests.pageobjects;

import uniovi.utils.SeleniumUtils;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;

public class PO_View {

    protected static PO_Properties p = new PO_Properties("messages");
    protected static int timeout = 3;

    public static int getTimeout() {
        return timeout;
    }

    public static void setTimeout(int timeout) {
        PO_View.timeout = timeout;
    }

    public static PO_Properties getP() {
        return p;
    }

    public static void setP(PO_Properties p) {
        PO_View.p = p;
    }

    /**
     * Espera por la visibilidad de un texto correspondiente a la propiedad key en el idioma locale en la vista actualmente cargandose en driver..
     *
     * @param driver: apuntando al navegador abierto actualmente.
     * @param string:    Texto esperado
     * @return Se retornará la lista de elementos resultantes de la búsqueda.
     */
    static public List<WebElement> checkKey(WebDriver driver, String string, int locale) {
        List<WebElement> elementos = SeleniumUtils.EsperaCargaPagina(driver, "text", string, getTimeout());
        return elementos;
    }

    /**
     * Espera por la visibilidad de un elemento/s en la vista actualmente cargandose en driver..
     *
     * @param driver: apuntando al navegador abierto actualmente.
     * @param type:
     * @param text:
     * @return Se retornará la lista de elementos resultantes de la búsqueda.
     */
    static public List<WebElement> checkElement(WebDriver driver, String type, String text) {
        List<WebElement> elementos = SeleniumUtils.EsperaCargaPagina(driver, type, text, getTimeout());
        return elementos;
    }

    static public void comprar(WebDriver driver, String objetoAComprar) {
        //Pinchamos en la opción de ver ofertas disponibles
        PO_View.checkElement(driver, "id", "offersManage").get(0).click();
        PO_View.checkElement(driver, "id", "seeAvailableOffersButton").get(0).click();
        //Esperamos a que aparezcan las ofertas
        PO_View.checkElement(driver, "id", "searchBox").get(0).sendKeys(objetoAComprar);
        // Buscamos con el cuadro vacio
        PO_View.checkElement(driver, "id", "searchButton").get(0).click();
        // Esperamos a que recarguen
        PO_View.checkElement(driver, "id", "searchBox");
        //Compramos la PS4
        PO_HomeView.checkElement(driver, "id", "offerBuyButton-" + objetoAComprar).get(0).click();
    }

    public static void agregarUsuario(WebDriver driver, String email, String nombre, String apellido) {
        //Vamos al formulario de registro
        PO_HomeView.clickOption(driver, "signup", "class", "btn btn-primary");
        //Rellenamos el formulario.
        PO_RegisterView.fillForm(driver, email, nombre, apellido, "77777",
                "77777");
        //Comprobamos que entramos en la sección privada
        PO_View.checkElement(driver, "text", "Ofertas del usuario");

        //Nos desconectamos
        PO_View.checkElement(driver, "id", "userOptionsButton").get(0).click();
        PO_View.checkElement(driver, "id", "logoutLink").get(0).click();
        PO_View.checkElement(driver, "id", "btn_login");
    }

    public static void crearConversacionPS4(WebDriver driver, String correo, String pass) {
        //Rellenamos el formulario
        PO_LoginView.fillForm(driver, correo, pass);
        //Pinchamos en la opción de ver ofertas disponibles
        PO_View.checkElement(driver, "id", "offersManage").get(0).click();
        PO_View.checkElement(driver, "id", "seeAvailableOffersButton").get(0).click();
        //Esperamos a que aparezcan las ofertas
        PO_View.checkElement(driver, "id", "searchBox").get(0).sendKeys("PS4");
        // Buscamos la oferta que queremos
        PO_View.checkElement(driver, "id", "searchButton").get(0).click();
        // Esperamos a que recarguen
        PO_View.checkElement(driver, "id", "sendMessageButton-PS4").get(0).click();
        // Escribimos el mensaje
        PO_View.checkElement(driver, "id", "textBox").get(0).sendKeys("Mensaje inicial de prueba");
        // Lo enviamos
        PO_View.checkElement(driver, "id", "sendButton").get(0).click();
        // Esperamos un par de segundos que recargue el chat
        SeleniumUtils.esperarSegundos(driver, 3);
        // Nos aseguramos que el campo de escribir esta vacio y vemos si esta el mensaje
        PO_View.checkElement(driver, "id", "textBox").get(0).clear();
        //Nos desconectamos
        PO_View.checkElement(driver, "id", "userOptionsButton").get(0).click();
        PO_View.checkElement(driver, "id", "logoutLink").get(0).click();
    }
}
