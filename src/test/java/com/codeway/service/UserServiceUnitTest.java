package com.codeway.service;

import com.codeway.dao.UserDao;
import com.codeway.filter.CORSFilter;
import com.codeway.model.User;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class UserServiceUnitTest {

	private MockMvc mockMvc;

	@Mock
	private UserDao userDao;

	@InjectMocks
	private UserService userService;

	@Before
	public void init() {
		MockitoAnnotations.initMocks(this);
		mockMvc = MockMvcBuilders
				.standaloneSetup(userService)
				.addFilters(new CORSFilter())
				.build();
	}


	@Test
	public void test_get_all_success() throws Exception {
		List<User> users = Arrays.asList(
				new User(1, "Foo"),
				new User(2, "Bar"));
		when(userService.getAllFromDao()).thenReturn(users);

		List<User> allFromDao = userService.getAllFromDao();

		verify(userDao, times(1)).findAll();
		assertEquals(2, allFromDao.size());

	}

}
