package getReq;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.testng.Assert;
import org.testng.annotations.Test;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.path.json.*;

public class GetApiData 
{
	
	// Variable declaration and data assignment 
	String userName = "rmccue";
	String RepoURL =  "https://api.github.com/users/" + userName + "/repos";
	String Language = "PHP";
	String BranchName = "master";
	int Page = 1;
	int PerPage = 3;
	int LoadRate = 5;
	
	@Test
	public void testValidateStatus() // This test will validate valid status code
	{
		Response resp = RestAssured.get(RepoURL);
		int code = resp.getStatusCode();
		System.out.println("System code is " + code);
		Assert.assertEquals(code, 200);
	}
	@Test
	public void testValidateTime() // This test will validate response should be under 5 seconds
	{
		Response resp = RestAssured.get(RepoURL);
		long time = resp.getTime();
		System.out.println("Execution Time is " + time);
		Assert.assertTrue( 5000 >= time );
	}
	@Test
	public void testValidateAllReposIsPublic() // This test will validate all repos is public
	{
		Response resp = RestAssured.get(RepoURL);

		String json = resp.getBody().asString();

		JsonPath jsonPath = new JsonPath(json);
		ArrayList dataArray = jsonPath.get("private");
		
		System.out.println(dataArray);
		
		for (int i = 0; i < dataArray.size(); i++)
		{ 		      
	        Assert.assertEquals(dataArray.get(i),false);
		}	
	}
	@Test
	public void testValidateReposPaginationPageValue() // This test will validate items on single page base on search criteria and sorting
	{
		Response resp = RestAssured.given()
				.queryParam("sort", "updated")
				.queryParam("page", Page)
				.queryParam("per_page", PerPage)
				.when().get(RepoURL).andReturn();
		
		String json = resp.getBody().asString();

		System.out.println(json);
		JsonPath jsonPath = new JsonPath(json);
		
		ArrayList dataArray = jsonPath.get("name");
		System.out.println(jsonPath);
		Assert.assertEquals(dataArray.size(),PerPage);
	}
	
	
	@Test
	public void testValidateLanguage() // This test will validate repos have specific language
	{
		Response resp = RestAssured.get(RepoURL);

		String json = resp.getBody().asString();

		System.out.println(json);
		JsonPath jsonPath = new JsonPath(json);
		
		ArrayList dataArray = jsonPath.get("language");


		Assert.assertTrue(dataArray.contains(Language));
	}
	
	
	@Test
	public void testValidateDefaultBranchName() // This test will validate repos default branch name
	{
		Response resp = RestAssured.get(RepoURL);

		String json = resp.getBody().asString();

		System.out.println(json);
		JsonPath jsonPath = new JsonPath(json);
		
		ArrayList dataArray = jsonPath.get("default_branch");

		Assert.assertTrue(dataArray.contains(BranchName));
	}
	@Test
	public void testValidateRepoOwnerName() // This test will validate repos owner name
	{
		Response resp = RestAssured.get(RepoURL);

		String json = resp.getBody().asString();

		System.out.println(json);
		JsonPath jsonPath = new JsonPath(json);
		
		ArrayList dataArray = jsonPath.get("owner.login");
		
		for (int i = 0; i < dataArray.size(); i++)
		{ 		      
	        Assert.assertEquals(dataArray.get(i),userName);
		}	
	}
	@Test
	public void testValidateUserLoad()  // This test will validate the load on user profile
	{
		Boolean loadStatus  = true;
		for (int i = 0; i < LoadRate; i++)
		{ 	
			Response resp = RestAssured.get(RepoURL);
			
			int code = resp.getStatusCode();
			
			System.out.println(code);
			
			if(code >= 200)
				loadStatus = false;
		}
		Assert.assertTrue(loadStatus);
	}
}
