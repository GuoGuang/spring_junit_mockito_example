## spring_junit_mockito_example
如何写好一个单元测试，使用junit_mockito完成


## 单元测试
单元测试是从测试**最小单元**为维度展开，也就是只测一个方法，只关注代码逻辑，其他的都不关心，这里我使用mockito+junit为案例演示

### 配置Mockito和MockMvc
```
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
}

```

### 查询所有数据

```
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
```



## 集成测试
集成测试每次测试都需要启动一次容器

```
@RunWith(SpringRunner.class)
@SpringBootTest
public class ResourceServiceTest {

    @Autowired
    private ResourceService resourceService;

    @Test
    public void findResourceByCondition() {
        QueryVO queryVO = new QueryVO();
        List<Resource> dictByCondition = resourceService.findResourceByCondition(new Resource(), queryVO);
        Assert.assertTrue(dictByCondition.size() > 0);

    }
}
```


实际开发中业务并非如此简单，可能涉及三方、MQ、等中间件服务，所以测试覆盖率一般80%左右