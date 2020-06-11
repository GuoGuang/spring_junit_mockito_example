package com.codeway.controller;

import com.codeway.config.WebConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.codeway.controller.UserController;
import com.codeway.filter.CORSFilter;
import com.codeway.model.User;
import com.codeway.service.UserService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.*;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import java.util.Arrays;
import java.util.List;
import static org.mockito.Mockito.*;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.junit.Assert.*;


/**
 * 单元测试
 */

@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {WebConfig.class})
public class UserControllerUnitTest {

    private MockMvc mockMvc;

	/**
	 * 被测类中用的类使用@Mock模拟
	 */
	@Mock
    private UserService userService;

	/**
	 * 模拟被测类
	 */
	@InjectMocks
    private UserController userController;

    @Before
    public void init(){
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders
                .standaloneSetup(userController)
                .addFilters(new CORSFilter())
                .build();
    }

    @Ignore
	@Test
	public void test_page() throws Exception {
		/*MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/welcomeController").param("pn", "5"))
				.andReturn();
		//请求成功以后，请求域中会有pageInfo；我们可以取出pageInfo进行验证
		MockHttpServletRequest request = result.getRequest();
		PageInfo pi = (PageInfo) request.getAttribute("pageInfo");
		assertEquals(1,pi.getPageNum());*/
	}


    @Test
    public void test_get_all_success() throws Exception {
        List<User> users = Arrays.asList(
                new User(1, "Foo"),
                new User(2, "Bar"));

        // 执行userService.getAll()时，返回users，这里是为了模拟/user接口的userService调用getAll返回的数据
        when(userService.getAll()).thenReturn(users);

        mockMvc.perform(get("/user"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].username", is("Foo")))
                .andExpect(jsonPath("$[1].id", is(2)))
                .andExpect(jsonPath("$[1].username", is("Bar")));

        // 验证调用次数
        verify(userService, times(1)).getAll();
        verifyNoMoreInteractions(userService);
    }


    @Test
    public void test_get_by_id_success() throws Exception {
        User user = new User(1, "巧克力酱");

        when(userService.findById(1)).thenReturn(user);

        mockMvc.perform(get("/user/{id}", 1))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.username", is("巧克力酱")));

        verify(userService, times(1)).findById(1);
        verifyNoMoreInteractions(userService);
    }

    @Test
    public void test_get_by_id_fail_404_not_found() throws Exception {
        when(userService.findById(1)).thenReturn(null);

        mockMvc.perform(get("/user/{id}", 1))
                .andExpect(status().isNotFound());

        verify(userService, times(1)).findById(1);
        verifyNoMoreInteractions(userService);
    }


    @Test
    public void test_create_user_success() throws Exception {
        User user = new User("芝士");

        when(userService.exists(user)).thenReturn(false);
        doNothing().when(userService).create(user);

        mockMvc.perform(
                post("/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(user)))
                .andExpect(status().isCreated())
                .andExpect(header().string("location", containsString("/user/0")));

        verify(userService, times(1)).exists(user);
        verify(userService, times(1)).create(user);
        verifyNoMoreInteractions(userService);
    }

    @Test
    public void test_create_user_fail_404_not_found() throws Exception {
        User user = new User("username exists");

        when(userService.exists(user)).thenReturn(true);

        mockMvc.perform(
                post("/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(user)))
                .andExpect(status().isConflict());

        verify(userService, times(1)).exists(user);
        verifyNoMoreInteractions(userService);
    }


    @Test
    public void test_update_user_success() throws Exception {
        User user = new User(1, "芝士");

        when(userService.findById(user.getId())).thenReturn(user);
        doNothing().when(userService).update(user);

        mockMvc.perform(
                put("/user/{id}", user.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(user)))
                .andExpect(status().isOk());

        verify(userService, times(1)).findById(user.getId());
        verify(userService, times(1)).update(user);
        verifyNoMoreInteractions(userService);
    }

    @Test
    public void test_update_user_fail_404_not_found() throws Exception {
        User user = new User(999, "user not found");

        when(userService.findById(user.getId())).thenReturn(null);

        mockMvc.perform(
                put("/user/{id}", user.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(user)))
                .andExpect(status().isNotFound());

        verify(userService, times(1)).findById(user.getId());
        verifyNoMoreInteractions(userService);
    }

    @Test
    public void test_delete_user_success() throws Exception {
        User user = new User(1, "芝士");

        when(userService.findById(user.getId())).thenReturn(user);
        doNothing().when(userService).delete(user.getId());

        mockMvc.perform(
                delete("/user/{id}", user.getId()))
                .andExpect(status().isOk());

        verify(userService, times(1)).findById(user.getId());
        verify(userService, times(1)).delete(user.getId());
        verifyNoMoreInteractions(userService);
    }

    @Test
    public void test_delete_user_fail_404_not_found() throws Exception {
        User user = new User(999, "user not found");

        when(userService.findById(user.getId())).thenReturn(null);

        mockMvc.perform(
                delete("/user/{id}", user.getId()))
                .andExpect(status().isNotFound());

        verify(userService, times(1)).findById(user.getId());
        verifyNoMoreInteractions(userService);
    }

    @Test
    public void test_cors_headers() throws Exception {
        mockMvc.perform(get("/user"))
                .andExpect(header().string("Access-Control-Allow-Origin", "*"))
                .andExpect(header().string("Access-Control-Allow-Methods", "POST, GET, PUT, OPTIONS, DELETE"))
                .andExpect(header().string("Access-Control-Allow-Headers", "*"))
                .andExpect(header().string("Access-Control-Max-Age", "3600"));
    }

    public static String asJsonString(final Object obj) {
        try {
            final ObjectMapper mapper = new ObjectMapper();
            return mapper.writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
