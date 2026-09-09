package com.babycharting.integration;

import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import com.jayway.jsonpath.JsonPath;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class BabyApiIntegrationTest {

	private static final String BABIES_URL = "/api/v1/babies";
	private static final long NON_EXISTENT_BABY_ID = 999_999_999L;

	@Autowired
	private MockMvc mockMvc;

	@Test
	void createBaby_returns201WithLocationAndBody() throws Exception {
		MvcResult result = mockMvc.perform(post(BABIES_URL)
						.contentType(MediaType.APPLICATION_JSON)
						.content(validCreateBabyJson("Ada", "Lovelace", "2024-06-15", "FEMALE")))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.firstName").value("Ada"))
				.andExpect(jsonPath("$.lastName").value("Lovelace"))
				.andExpect(jsonPath("$.dateOfBirth").value("2024-06-15"))
				.andExpect(jsonPath("$.sex").value("FEMALE"))
				.andExpect(jsonPath("$.createdAt", notNullValue()))
				.andExpect(jsonPath("$.updatedAt", notNullValue()))
				.andExpect(jsonPath("$.id").isNumber())
				.andReturn();

		String responseBody = result.getResponse().getContentAsString();
		Long id = JsonPath.parse(responseBody).read("$.id", Long.class);
		String location = result.getResponse().getHeader("Location");

		assertNotNull(location);
		assertTrue(location.endsWith("/api/v1/babies/" + id));
	}

	@Test
	void findAll_returnsCreatedBabies() throws Exception {
		createBabyAndReturnId("Ada", "Lovelace", "2024-06-15", "FEMALE");
		createBabyAndReturnId("Grace", "Hopper", "2024-07-01", "FEMALE");

		mockMvc.perform(get(BABIES_URL))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.page").value(0))
				.andExpect(jsonPath("$.size").value(20))
				.andExpect(jsonPath("$.totalElements").value(2))
				.andExpect(jsonPath("$.totalPages").value(1))
				.andExpect(jsonPath("$.first").value(true))
				.andExpect(jsonPath("$.last").value(true))
				.andExpect(jsonPath("$.content.length()").value(2))
				.andExpect(jsonPath("$.content[0].firstName").value("Grace"))
				.andExpect(jsonPath("$.content[1].firstName").value("Ada"));
	}

	@Test
	void findAll_withCustomPageAndSize_returnsRequestedPage() throws Exception {
		createBabyAndReturnId("Baby", "One", "2024-01-01", "FEMALE");
		createBabyAndReturnId("Baby", "Two", "2024-02-01", "FEMALE");
		createBabyAndReturnId("Baby", "Three", "2024-03-01", "FEMALE");

		mockMvc.perform(get(BABIES_URL).param("page", "0").param("size", "2"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.page").value(0))
				.andExpect(jsonPath("$.size").value(2))
				.andExpect(jsonPath("$.totalElements").value(3))
				.andExpect(jsonPath("$.totalPages").value(2))
				.andExpect(jsonPath("$.first").value(true))
				.andExpect(jsonPath("$.last").value(false))
				.andExpect(jsonPath("$.content.length()").value(2));
	}

	@Test
	void findAll_secondPage_returnsRemainingItems() throws Exception {
		createBabyAndReturnId("Baby", "One", "2024-01-01", "FEMALE");
		createBabyAndReturnId("Baby", "Two", "2024-02-01", "FEMALE");
		createBabyAndReturnId("Baby", "Three", "2024-03-01", "FEMALE");

		mockMvc.perform(get(BABIES_URL).param("page", "1").param("size", "2"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.page").value(1))
				.andExpect(jsonPath("$.size").value(2))
				.andExpect(jsonPath("$.totalElements").value(3))
				.andExpect(jsonPath("$.totalPages").value(2))
				.andExpect(jsonPath("$.first").value(false))
				.andExpect(jsonPath("$.last").value(true))
				.andExpect(jsonPath("$.content.length()").value(1));
	}

	@Test
	void findAll_withNoBabies_returnsEmptyPage() throws Exception {
		mockMvc.perform(get(BABIES_URL))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.content.length()").value(0))
				.andExpect(jsonPath("$.totalElements").value(0))
				.andExpect(jsonPath("$.totalPages").value(0))
				.andExpect(jsonPath("$.first").value(true))
				.andExpect(jsonPath("$.last").value(true));
	}

	@Test
	void findAll_withSortByFirstName_returnsSortedResults() throws Exception {
		createBabyAndReturnId("Zoe", "Alpha", "2024-01-01", "FEMALE");
		createBabyAndReturnId("Amy", "Beta", "2024-02-01", "FEMALE");

		mockMvc.perform(get(BABIES_URL).param("sort", "firstName,asc"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.content[0].firstName").value("Amy"))
				.andExpect(jsonPath("$.content[1].firstName").value("Zoe"));
	}

	@Test
	void findAll_withInvalidSortField_returns400() throws Exception {
		mockMvc.perform(get(BABIES_URL).param("sort", "invalidField,asc"))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.detail").value("Invalid sort property: invalidField"));
	}

	@Test
	void findAll_withSizeExceedingMax_isClampedToMaxPageSize() throws Exception {
		mockMvc.perform(get(BABIES_URL).param("size", "101"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.size").value(100));
	}

	@Test
	void findById_returnsBaby() throws Exception {
		Long id = createBabyAndReturnId("Ada", "Lovelace", "2024-06-15", "FEMALE");

		mockMvc.perform(get(babyUrl(id)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(id))
				.andExpect(jsonPath("$.firstName").value("Ada"))
				.andExpect(jsonPath("$.lastName").value("Lovelace"))
				.andExpect(jsonPath("$.dateOfBirth").value("2024-06-15"))
				.andExpect(jsonPath("$.sex").value("FEMALE"));
	}

	@Test
	void findById_whenNotFound_returns404() throws Exception {
		mockMvc.perform(get(babyUrl(NON_EXISTENT_BABY_ID)))
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.detail").value("Baby not found with id: " + NON_EXISTENT_BABY_ID));
	}

	@Test
	void updateBaby_partialUpdate_returns200() throws Exception {
		Long id = createBabyAndReturnId("Ada", "Lovelace", "2024-06-15", "FEMALE");

		mockMvc.perform(patch(babyUrl(id))
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{
								  "lastName": "Byron"
								}
								"""))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(id))
				.andExpect(jsonPath("$.firstName").value("Ada"))
				.andExpect(jsonPath("$.lastName").value("Byron"))
				.andExpect(jsonPath("$.dateOfBirth").value("2024-06-15"))
				.andExpect(jsonPath("$.sex").value("FEMALE"));
	}

	@Test
	void deleteBaby_returns204AndRemovesBaby() throws Exception {
		Long id = createBabyAndReturnId("Ada", "Lovelace", "2024-06-15", "FEMALE");

		mockMvc.perform(delete(babyUrl(id)))
				.andExpect(status().isNoContent());

		mockMvc.perform(get(babyUrl(id)))
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.detail").value("Baby not found with id: " + id));
	}

	@Test
	void createBaby_withBlankFirstName_returns400() throws Exception {
		mockMvc.perform(post(BABIES_URL)
						.contentType(MediaType.APPLICATION_JSON)
						.content(validCreateBabyJson("", "Lovelace", "2024-06-15", "FEMALE")))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.detail").value("Validation failed"))
				.andExpect(jsonPath("$.errors.firstName").value("must not be blank"));
	}

	@Test
	void createBaby_withFutureDateOfBirth_returns400() throws Exception {
		String futureDate = LocalDate.now().plusDays(1).toString();

		mockMvc.perform(post(BABIES_URL)
						.contentType(MediaType.APPLICATION_JSON)
						.content(validCreateBabyJson("Ada", "Lovelace", futureDate, "FEMALE")))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.detail").value("Validation failed"))
				.andExpect(jsonPath("$.errors.dateOfBirth").value("must be a date in the past or in the present"));
	}

	@Test
	void updateBaby_withEmptyBody_returns400() throws Exception {
		Long id = createBabyAndReturnId("Ada", "Lovelace", "2024-06-15", "FEMALE");

		mockMvc.perform(patch(babyUrl(id))
						.contentType(MediaType.APPLICATION_JSON)
						.content("{}"))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.detail").value("At least one field must be provided"));
	}

	@Test
	void updateBaby_whenNotFound_returns404() throws Exception {
		mockMvc.perform(patch(babyUrl(NON_EXISTENT_BABY_ID))
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{
								  "lastName": "Byron"
								}
								"""))
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.detail").value("Baby not found with id: " + NON_EXISTENT_BABY_ID));
	}

	@Test
	void deleteBaby_whenNotFound_returns404() throws Exception {
		mockMvc.perform(delete(babyUrl(NON_EXISTENT_BABY_ID)))
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.detail").value("Baby not found with id: " + NON_EXISTENT_BABY_ID));
	}

	@Test
	void updateBaby_withBlankFirstName_returns400() throws Exception {
		Long id = createBabyAndReturnId("Ada", "Lovelace", "2024-06-15", "FEMALE");

		mockMvc.perform(patch(babyUrl(id))
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{
								  "firstName": ""
								}
								"""))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.detail").value("firstName must not be blank"));
	}

	@Test
	void updateBaby_withBlankLastName_returns400() throws Exception {
		Long id = createBabyAndReturnId("Ada", "Lovelace", "2024-06-15", "FEMALE");

		mockMvc.perform(patch(babyUrl(id))
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{
								  "lastName": ""
								}
								"""))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.detail").value("lastName must not be blank"));
	}

	@Test
	void updateBaby_withFutureDateOfBirth_returns400() throws Exception {
		Long id = createBabyAndReturnId("Ada", "Lovelace", "2024-06-15", "FEMALE");
		String futureDate = LocalDate.now().plusDays(1).toString();

		mockMvc.perform(patch(babyUrl(id))
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{
								  "dateOfBirth": "%s"
								}
								""".formatted(futureDate)))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.detail").value("Validation failed"))
				.andExpect(jsonPath("$.errors.dateOfBirth").value("must be a date in the past or in the present"));
	}

	@Test
	void createBaby_withInvalidSex_returns400() throws Exception {
		mockMvc.perform(post(BABIES_URL)
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{
								  "firstName": "Ada",
								  "lastName": "Lovelace",
								  "dateOfBirth": "2024-06-15",
								  "sex": "INVALID"
								}
								"""))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.detail").value("Validation failed"))
				.andExpect(jsonPath("$.errors.sex").value("Invalid value"));
	}

	@Test
	void createBaby_withoutSex_returns201() throws Exception {
		mockMvc.perform(post(BABIES_URL)
						.contentType(MediaType.APPLICATION_JSON)
						.content(validCreateBabyJson("Ada", "Lovelace", "2024-06-15", null)))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.firstName").value("Ada"))
				.andExpect(jsonPath("$.lastName").value("Lovelace"))
				.andExpect(jsonPath("$.dateOfBirth").value("2024-06-15"))
				.andExpect(jsonPath("$.sex").value(nullValue()));
	}

	private Long createBabyAndReturnId(
			String firstName, String lastName, String dateOfBirth, String sex) throws Exception {
		MvcResult result = mockMvc.perform(post(BABIES_URL)
						.contentType(MediaType.APPLICATION_JSON)
						.content(validCreateBabyJson(firstName, lastName, dateOfBirth, sex)))
				.andExpect(status().isCreated())
				.andReturn();

		return JsonPath.parse(result.getResponse().getContentAsString()).read("$.id", Long.class);
	}

	private String babyUrl(Long id) {
		return BABIES_URL + "/" + id;
	}

	private String validCreateBabyJson(
			String firstName, String lastName, String dateOfBirth, String sex) {
		if (sex == null) {
			return """
					{
					  "firstName": "%s",
					  "lastName": "%s",
					  "dateOfBirth": "%s"
					}
					""".formatted(firstName, lastName, dateOfBirth);
		}

		return """
				{
				  "firstName": "%s",
				  "lastName": "%s",
				  "dateOfBirth": "%s",
				  "sex": "%s"
				}
				""".formatted(firstName, lastName, dateOfBirth, sex);
	}

}
