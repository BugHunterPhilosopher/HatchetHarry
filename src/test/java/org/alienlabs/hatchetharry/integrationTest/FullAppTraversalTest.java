package org.alienlabs.hatchetharry.integrationTest;

import static junit.framework.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.concurrent.TimeUnit;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FullAppTraversalTest
{
	private static WebDriver chromeDriver1;
	private static WebDriver chromeDriver2;

	private static final String PORT = "8088";
	private static final String HOST = "localhost";

	private static final String SHOW_AND_OPEN_MOBILE_MENUBAR = "jQuery('#jMenu').hide(); jQuery('.dropdownmenu').show(); jQuery('.dropdownmenu:first').click();";

	private static final Logger LOGGER = LoggerFactory.getLogger(FullAppTraversalTest.class);

	@BeforeClass
	public static void setUpClass()
	{
		System.setProperty("webdriver.chrome.driver", "/home/nostromo/chromedriver");
		FullAppTraversalTest.chromeDriver1 = new ChromeDriver();
		FullAppTraversalTest.chromeDriver1.get(FullAppTraversalTest.HOST + ":"
				+ FullAppTraversalTest.PORT + "/");

		FullAppTraversalTest.chromeDriver2 = new ChromeDriver();
		FullAppTraversalTest.chromeDriver2.get(FullAppTraversalTest.HOST + ":"
				+ FullAppTraversalTest.PORT + "/");
	}

	@AfterClass
	public static void tearDownClass()
	{
		FullAppTraversalTest.chromeDriver1.quit();
		FullAppTraversalTest.chromeDriver2.quit();
	}

	@Test
	public void testFullAppTraversal() throws InterruptedException
	{
		FullAppTraversalTest.chromeDriver1.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
		FullAppTraversalTest.chromeDriver2.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);

		// Create a game in Chrome 1
		FullAppTraversalTest.waitForJQueryProcessing(FullAppTraversalTest.chromeDriver1, 60);

		((JavascriptExecutor)FullAppTraversalTest.chromeDriver1)
				.executeScript(FullAppTraversalTest.SHOW_AND_OPEN_MOBILE_MENUBAR);

		FullAppTraversalTest.chromeDriver1.findElement(By.id("createGameLinkResponsive")).click();
		FullAppTraversalTest.chromeDriver1.findElement(By.id("name")).clear();
		FullAppTraversalTest.chromeDriver1.findElement(By.id("name")).sendKeys("Zala");
		new Select(FullAppTraversalTest.chromeDriver1.findElement(By.id("sideInput")))
				.selectByVisibleText("infrared");
		new Select(FullAppTraversalTest.chromeDriver1.findElement(By.id("decks")))
				.selectByVisibleText("Aura Bant");

		final String gameId = FullAppTraversalTest.chromeDriver1.findElement(By.id("gameId"))
				.getText();

		FullAppTraversalTest.chromeDriver1.findElement(By.id("createSubmit")).click();

		// Join a game in Chrome 2
		FullAppTraversalTest.waitForJQueryProcessing(FullAppTraversalTest.chromeDriver2, 60);

		((JavascriptExecutor)FullAppTraversalTest.chromeDriver2)
				.executeScript(FullAppTraversalTest.SHOW_AND_OPEN_MOBILE_MENUBAR);

		FullAppTraversalTest.chromeDriver2.findElement(By.id("joinGameLinkResponsive")).click();
		FullAppTraversalTest.chromeDriver2.findElement(By.id("name")).clear();
		FullAppTraversalTest.chromeDriver2.findElement(By.id("name")).sendKeys("Zala");
		new Select(FullAppTraversalTest.chromeDriver2.findElement(By.id("sideInput")))
				.selectByVisibleText("ultraviolet");
		new Select(FullAppTraversalTest.chromeDriver2.findElement(By.id("decks")))
				.selectByVisibleText("Aura Bant");
		FullAppTraversalTest.chromeDriver2.findElement(By.id("gameIdInput")).clear();
		FullAppTraversalTest.chromeDriver2.findElement(By.id("gameIdInput")).sendKeys(gameId);

		FullAppTraversalTest.chromeDriver2.findElement(By.id("joinSubmit")).click();

		// Assert no card present
		assertTrue(FullAppTraversalTest.chromeDriver1.findElements(By.cssSelector(".ui-draggable"))
				.isEmpty());
		assertTrue(FullAppTraversalTest.chromeDriver2.findElements(By.cssSelector(".ui-draggable"))
				.isEmpty());

		// Find first hand card name
		final String cardName = FullAppTraversalTest.chromeDriver1
				.findElements(By.cssSelector(".cross-link:nth-child(1) img")).get(0)
				.getAttribute("name");
		LOGGER.info("card name : " + cardName);

		// Play a card in Chrome1
		FullAppTraversalTest.chromeDriver1.findElement(By.id("playCardLink0")).click();

		// Verify card is present on the battlefield
		Thread.sleep(6000);
		assertTrue(FullAppTraversalTest.chromeDriver1.findElements(By.cssSelector(".ui-draggable"))
				.size() == 1);
		assertTrue(FullAppTraversalTest.chromeDriver2.findElements(By.cssSelector(".ui-draggable"))
				.size() == 1);

		// Verify name of the card on the battlefield
		assertTrue(cardName.equals(FullAppTraversalTest.chromeDriver1.findElement(
				By.cssSelector(".ui-draggable")).getAttribute("name")));
		assertTrue(cardName.equals(FullAppTraversalTest.chromeDriver2.findElement(
				By.cssSelector(".ui-draggable")).getAttribute("name")));

		// Verify card is untapped
		assertFalse(FullAppTraversalTest.chromeDriver1
				.findElements(By.cssSelector("img[id^='card']")).get(0).getAttribute("style")
				.contains("transform"));
		assertFalse(FullAppTraversalTest.chromeDriver2
				.findElements(By.cssSelector("img[id^='card']")).get(0).getAttribute("style")
				.contains("transform"));

		// Tap card
		FullAppTraversalTest.chromeDriver1.findElement(By.cssSelector("img[id^='tapHandleImage']"))
				.click();
		Thread.sleep(10000);

		// Verify card is tapped
		assertTrue(FullAppTraversalTest.chromeDriver1
				.findElements(By.cssSelector("img[id^='card']")).get(0).getAttribute("style")
				.contains("rotate(90deg)"));
		assertTrue(FullAppTraversalTest.chromeDriver2
				.findElements(By.cssSelector("img[id^='card']")).get(0).getAttribute("style")
				.contains("rotate(90deg)"));

		// Assert graveyard not visible
		assertTrue(FullAppTraversalTest.chromeDriver1.findElements(By.id("graveyard-page-wrap"))
				.isEmpty());

		// Drag card to graveyard
		final WebElement draggable = FullAppTraversalTest.chromeDriver1.findElement(By
				.cssSelector("img[id^='handleImage']"));
		final WebElement to = FullAppTraversalTest.chromeDriver1.findElement(By
				.id("putToGraveyard"));
		new Actions(FullAppTraversalTest.chromeDriver1).dragAndDrop(draggable, to).build()
				.perform();

		Thread.sleep(10000);

		// Assert graveyard is visible and contains one card
		assertFalse(FullAppTraversalTest.chromeDriver1.findElements(By.id("graveyard-page-wrap"))
				.isEmpty());
		assertTrue(FullAppTraversalTest.chromeDriver1.findElements(
				By.cssSelector(".graveyard-cross-link")).size() == 1);

		// Verify name of the card in the graveyard
		assertTrue(cardName.equals(FullAppTraversalTest.chromeDriver1
				.findElements(By.cssSelector(".graveyard-cross-link:nth-child(1) img")).get(0)
				.getAttribute("name")));
	}

	public static boolean waitForJQueryProcessing(final WebDriver driver, final int timeOutInSeconds)
	{
		boolean jQcondition = false;
		try
		{
			new WebDriverWait(driver, timeOutInSeconds)
			{
			}.until(new ExpectedCondition<Boolean>()
			{

				@Override
				public Boolean apply(final WebDriver driverObject)
				{
					return (Boolean)((JavascriptExecutor)driverObject)
							.executeScript("return jQuery.active == 0");
				}
			});
			jQcondition = (Boolean)((JavascriptExecutor)driver)
					.executeScript("return window.jQuery != undefined && jQuery.active === 0");
			return jQcondition;
		}
		catch (final Exception e)
		{
			e.printStackTrace();
		}
		return jQcondition;
	}

}
