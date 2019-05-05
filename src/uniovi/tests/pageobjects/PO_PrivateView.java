package uniovi.tests.pageobjects;

import uniovi.utils.SeleniumUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class PO_PrivateView extends PO_NavView {
    static public void fillFormAddOffer(WebDriver driver, String titulo, String detalles, double precio) {
        //Esperamos 5 segundo a que carge el DOM porque en algunos equipos falla
        SeleniumUtils.esperarSegundos(driver, 1);
        //Rellenemos el campo de titulo
        WebElement title = driver.findElement(By.name("title"));
        title.clear();
        title.sendKeys(titulo);
        //Rellenemos el campo de detalles
        WebElement description = driver.findElement(By.name("details"));
        description.clear();
        description.sendKeys(detalles);
        //Rellenemos el campo de precio
        WebElement price = driver.findElement(By.name("value"));
        price.clear();
        price.sendKeys(String.valueOf(precio));
        By boton = By.id("addOffer");
        driver.findElement(boton).click();
    }
}