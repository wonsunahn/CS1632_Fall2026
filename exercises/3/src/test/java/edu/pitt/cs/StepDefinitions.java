package edu.pitt.cs;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import static org.junit.Assert.*;

public class StepDefinitions {
	private RentACat r;
	private String listResult;
	
	// TODO: Add more member variables and methods as necessary

	@Given("a rent-a-cat facility")
	public void aRentACatFacility() {
		r = RentACat.createInstance(InstanceType.SOLUTION);
	}
	
	@Given("no cats")
	public void noCats() {
		// nothing to do really
	}
	
	@Given("a cat with ID {int} and name {string}")
	public void aCatWithIDAndName(Integer id, String name) {
		// TODO: Create a SOLUTION cat using Cat.createInstance method and add to r.
	}
	
	@When("I list the cat(s)")
	public void iListTheCats() {
		// TODO: Generate a listing of cats and assign to listResult.
	}
	
	@When("I rent cat number {int}")
	public void iRentCatNumber(Integer id) {
		// TODO: Rent out the cat with given id.
	}
	
	@Then("the listing is: {string}")
	public void theListingIs(String expectedResult) {
		// Replace "\n" escape sequence in the expected cat list in Gherkin feature file with actual newline character.
		expectedResult = expectedResult.replaceAll("\\\\n", "\n");

		// TODO: Replace fail() with a check whether listResult is equal to expectedResult.
		fail();
	}
	
	@Then("the rent is successful")
	public void theRentIsSuccessful() {
		// TODO: Replace fail() with a check whether the previous rent step was successful.
		// The rent is successful if rentCat returned true.
		fail();
	}

	@Then("the rent is unsuccessful")
	public void theRentIsUnsuccessful() {
		// TODO: Replace fail() with a check whether the previous rent step was unsuccessful.
		// The rent is unsuccessful if rentCat returned false.
		fail();
	}
}
