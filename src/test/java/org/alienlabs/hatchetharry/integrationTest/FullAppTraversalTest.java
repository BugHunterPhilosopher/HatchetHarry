package org.alienlabs.hatchetharry.integrationTest;

import java.util.concurrent.TimeUnit;

import org.junit.AfterClass;
import org.junit.Assert;
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

		// Assert that no card is present on battlefield
		Assert.assertTrue(FullAppTraversalTest.chromeDriver1.findElements(
				By.cssSelector(".ui-draggable")).isEmpty());
		Assert.assertTrue(FullAppTraversalTest.chromeDriver2.findElements(
				By.cssSelector(".ui-draggable")).isEmpty());

		// Verify that the hands contains 7 cards
		Assert.assertTrue(FullAppTraversalTest.chromeDriver1.findElements(
				By.cssSelector(".cross-link img")).size() == 7);
		Assert.assertTrue(FullAppTraversalTest.chromeDriver2.findElements(
				By.cssSelector(".cross-link img")).size() == 7);

		// Find first hand card name of Chrome1
		String battlefieldCardName = FullAppTraversalTest.chromeDriver1
				.findElements(By.cssSelector(".cross-link:nth-child(1) img")).get(0)
				.getAttribute("name");

		// Play a card in Chrome1
		FullAppTraversalTest.chromeDriver1.findElement(By.id("playCardLink0")).click();

		// Verify that the hand contains only 6 cards, now
		Thread.sleep(6000);
		Assert.assertTrue(FullAppTraversalTest.chromeDriver1.findElements(
				By.cssSelector(".cross-link img")).size() == 6);

		// Verify that card is present on the battlefield
		Assert.assertTrue(FullAppTraversalTest.chromeDriver1.findElements(
				By.cssSelector(".ui-draggable")).size() == 1);
		Assert.assertTrue(FullAppTraversalTest.chromeDriver2.findElements(
				By.cssSelector(".ui-draggable")).size() == 1);

		// Verify the name of the card on the battlefield
		Assert.assertTrue(battlefieldCardName.equals(FullAppTraversalTest.chromeDriver1
				.findElement(By.cssSelector(".ui-draggable")).getAttribute("name")));
		Assert.assertTrue(battlefieldCardName.equals(FullAppTraversalTest.chromeDriver2
				.findElement(By.cssSelector(".ui-draggable")).getAttribute("name")));

		// Verify that the card is untapped
		Assert.assertFalse(FullAppTraversalTest.chromeDriver1
				.findElements(By.cssSelector("img[id^='card']")).get(0).getAttribute("style")
				.contains("transform"));
		Assert.assertFalse(FullAppTraversalTest.chromeDriver2
				.findElements(By.cssSelector("img[id^='card']")).get(0).getAttribute("style")
				.contains("transform"));

		// Tap card
		FullAppTraversalTest.chromeDriver1.findElement(By.cssSelector("img[id^='tapHandleImage']"))
				.click();
		Thread.sleep(10000);

		// Verify card is tapped
		Assert.assertTrue(FullAppTraversalTest.chromeDriver1
				.findElements(By.cssSelector("img[id^='card']")).get(0).getAttribute("style")
				.contains("rotate(90deg)"));
		Assert.assertTrue(FullAppTraversalTest.chromeDriver2
				.findElements(By.cssSelector("img[id^='card']")).get(0).getAttribute("style")
				.contains("rotate(90deg)"));

		// Assert that graveyard is not visible
		Assert.assertTrue(FullAppTraversalTest.chromeDriver1.findElements(
				By.id("graveyard-page-wrap")).isEmpty());

		// Drag card to graveyard
		WebElement draggable = FullAppTraversalTest.chromeDriver1.findElement(By
				.cssSelector("img[id^='handleImage']"));
		WebElement to = FullAppTraversalTest.chromeDriver1.findElement(By.id("putToGraveyard"));
		new Actions(FullAppTraversalTest.chromeDriver1).dragAndDrop(draggable, to).build()
				.perform();

		Thread.sleep(10000);

		// Assert graveyard is visible and contains one card
		Assert.assertFalse(FullAppTraversalTest.chromeDriver1.findElements(
				By.id("graveyard-page-wrap")).isEmpty());
		Assert.assertTrue(FullAppTraversalTest.chromeDriver1.findElements(
				By.cssSelector(".graveyard-cross-link")).size() == 1);

		// Verify name of the card in the graveyard
		Assert.assertTrue(battlefieldCardName.equals(FullAppTraversalTest.chromeDriver1
				.findElements(By.cssSelector(".graveyard-cross-link:nth-child(1) img")).get(0)
				.getAttribute("name")));

		// Play card from graveyard
		((JavascriptExecutor)FullAppTraversalTest.chromeDriver1)
				.executeScript(FullAppTraversalTest.SHOW_AND_OPEN_MOBILE_MENUBAR);
		FullAppTraversalTest.chromeDriver1
				.findElement(By.id("playCardFromGraveyardLinkResponsive")).click();
		Thread.sleep(8000);

		// Verify the name of the card on the battlefield
		Assert.assertTrue(battlefieldCardName.equals(FullAppTraversalTest.chromeDriver1
				.findElement(By.cssSelector(".ui-draggable")).getAttribute("name")));
		Assert.assertTrue(battlefieldCardName.equals(FullAppTraversalTest.chromeDriver2
				.findElement(By.cssSelector(".ui-draggable")).getAttribute("name")));

		// Assert that the graveyard is empty
		Assert.assertFalse(FullAppTraversalTest.chromeDriver1.findElements(
				By.id("graveyard-page-wrap")).isEmpty());
		Assert.assertTrue(FullAppTraversalTest.chromeDriver1.findElements(
				By.cssSelector(".graveyard-cross-link")).isEmpty());

		// Drag card to hand
		draggable = FullAppTraversalTest.chromeDriver1.findElement(By
				.cssSelector("img[id^='handleImage']"));
		to = FullAppTraversalTest.chromeDriver1.findElement(By.id("putToHand"));
		new Actions(FullAppTraversalTest.chromeDriver1).dragAndDrop(draggable, to).build()
				.perform();

		Thread.sleep(10000);

		// Assert that the hand contains 7 cards again
		Assert.assertTrue(FullAppTraversalTest.chromeDriver1.findElements(
				By.cssSelector(".cross-link img")).size() == 7);


		// Reveal top card of library
		((JavascriptExecutor)FullAppTraversalTest.chromeDriver1)
				.executeScript(FullAppTraversalTest.SHOW_AND_OPEN_MOBILE_MENUBAR);
		FullAppTraversalTest.chromeDriver1.findElement(By.id("revealTopLibraryCardLinkResponsive"))
				.click();
		Thread.sleep(8000);

		// Get top card name
		battlefieldCardName = FullAppTraversalTest.chromeDriver1.findElement(
				By.id("topLibraryCard")).getAttribute("name");

		// Verify that the card name is the same in the second browser
		Assert.assertTrue(battlefieldCardName.equals(FullAppTraversalTest.chromeDriver2
				.findElement(By.id("topLibraryCard")).getAttribute("name")));

		// Click on the button "Do nothing"
		FullAppTraversalTest.chromeDriver1.findElement(By.id("doNothing")).click();
		FullAppTraversalTest.chromeDriver2.findElement(By.id("doNothing")).click();
		Thread.sleep(8000);

		// Assert that no card is present on battlefield
		Assert.assertTrue(FullAppTraversalTest.chromeDriver1.findElements(
				By.cssSelector(".ui-draggable")).isEmpty());
		Assert.assertTrue(FullAppTraversalTest.chromeDriver2.findElements(
				By.cssSelector(".ui-draggable")).isEmpty());

		// Reveal again
		((JavascriptExecutor)FullAppTraversalTest.chromeDriver1)
				.executeScript(FullAppTraversalTest.SHOW_AND_OPEN_MOBILE_MENUBAR);
		FullAppTraversalTest.chromeDriver1.findElement(By.id("revealTopLibraryCardLinkResponsive"))
				.click();
		Thread.sleep(8000);

		// Assert that the card is the same
		Assert.assertTrue(battlefieldCardName.equals(FullAppTraversalTest.chromeDriver1
				.findElement(By.id("topLibraryCard")).getAttribute("name")));
		Assert.assertTrue(battlefieldCardName.equals(FullAppTraversalTest.chromeDriver2
				.findElement(By.id("topLibraryCard")).getAttribute("name")));

		// Put to battlefield
		FullAppTraversalTest.chromeDriver1.findElement(By.id("putToBattlefield")).click();
		FullAppTraversalTest.chromeDriver2.findElement(By.id("doNothing")).click();
		Thread.sleep(8000);

		// Verify that the card is present on the battlefield
		Assert.assertTrue(FullAppTraversalTest.chromeDriver1.findElements(
				By.cssSelector(".ui-draggable")).size() == 1);
		Assert.assertTrue(FullAppTraversalTest.chromeDriver2.findElements(
				By.cssSelector(".ui-draggable")).size() == 1);

		// Assert that the card on the battlefield is the same
		Assert.assertTrue(battlefieldCardName.equals(FullAppTraversalTest.chromeDriver1
				.findElement(By.cssSelector(".ui-draggable")).getAttribute("name")));
		Assert.assertTrue(battlefieldCardName.equals(FullAppTraversalTest.chromeDriver2
				.findElement(By.cssSelector(".ui-draggable")).getAttribute("name")));

		// Reveal top card of library
		((JavascriptExecutor)FullAppTraversalTest.chromeDriver1)
				.executeScript(FullAppTraversalTest.SHOW_AND_OPEN_MOBILE_MENUBAR);
		FullAppTraversalTest.chromeDriver1.findElement(By.id("revealTopLibraryCardLinkResponsive"))
				.click();
		Thread.sleep(8000);

		// Put to hand
		FullAppTraversalTest.chromeDriver1.findElement(By.id("putToHand")).click();
		FullAppTraversalTest.chromeDriver2.findElement(By.id("doNothing")).click();
		Thread.sleep(8000);

		// Assert that the hand contains 8 cards
		Assert.assertTrue(FullAppTraversalTest.chromeDriver1.findElements(
				By.cssSelector(".cross-link img")).size() == 8);

		// Verify that there is still one card on the battlefield
		Assert.assertTrue(FullAppTraversalTest.chromeDriver1.findElements(
				By.cssSelector(".ui-draggable")).size() == 1);
		Assert.assertTrue(FullAppTraversalTest.chromeDriver2.findElements(
				By.cssSelector(".ui-draggable")).size() == 1);

		// Reveal again
		((JavascriptExecutor)FullAppTraversalTest.chromeDriver1)
				.executeScript(FullAppTraversalTest.SHOW_AND_OPEN_MOBILE_MENUBAR);
		FullAppTraversalTest.chromeDriver1.findElement(By.id("revealTopLibraryCardLinkResponsive"))
				.click();
		Thread.sleep(8000);

		// Get top card name
		final String graveyardCardName = FullAppTraversalTest.chromeDriver1.findElement(
				By.id("topLibraryCard")).getAttribute("name");

		// Put to graveyard
		FullAppTraversalTest.chromeDriver1.findElement(By.id("putToGraveyard")).click();
		FullAppTraversalTest.chromeDriver2.findElement(By.id("doNothing")).click();
		Thread.sleep(8000);

		// Assert graveyard is visible and contains one card
		Assert.assertFalse(FullAppTraversalTest.chromeDriver1.findElements(
				By.id("graveyard-page-wrap")).isEmpty());
		Assert.assertTrue(FullAppTraversalTest.chromeDriver1.findElements(
				By.cssSelector(".graveyard-cross-link")).size() == 1);

		// Verify name of the card in the graveyard
		Assert.assertTrue(graveyardCardName.equals(FullAppTraversalTest.chromeDriver1
				.findElements(By.cssSelector(".graveyard-cross-link:nth-child(1) img")).get(0)
				.getAttribute("name")));

		// Verify that there is still one card on the battlefield
		Assert.assertTrue(FullAppTraversalTest.chromeDriver1.findElements(
				By.cssSelector(".ui-draggable")).size() == 1);
		Assert.assertTrue(FullAppTraversalTest.chromeDriver2.findElements(
				By.cssSelector(".ui-draggable")).size() == 1);

		// Verify the name of the card on the battlefield
		Assert.assertTrue(battlefieldCardName.equals(FullAppTraversalTest.chromeDriver1
				.findElement(By.cssSelector(".ui-draggable")).getAttribute("name")));
		Assert.assertTrue(battlefieldCardName.equals(FullAppTraversalTest.chromeDriver2
				.findElement(By.cssSelector(".ui-draggable")).getAttribute("name")));
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
