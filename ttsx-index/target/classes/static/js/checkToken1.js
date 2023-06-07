(function () {
  //使用axios
  Vue.prototype.axios = axios;

  //在每次请求时，前端携带token到请求头中
  axios.interceptors.request.use(config => {
    //从localStorage或vuex中获取token
    const token = localStorage.getItem('token'); //||this.$stire.stste.token;
    //如果token，就添加到请求头中
    if (token) {
      config.headers.Authorization = token;
    }
    console.log(token);
    console.log(config);
    return config;
  }, error => {
    return Promise.reject(error);
  });

  //在每次响应时，前端检查token是否有效或过期
  // 添加响应拦截器
  axios.interceptors.response.use(function (response) {
    // 对响应数据做点什么
    return response;
    console.log(response)
  }, function (error) {
    // 对响应错误做点什么
    console.log(error)
    if (error.response.status === 401) {
      // 未登录，跳转到登录页面
      //TODO:利用框架来做登陆的跳转
      // router.push('/login');
      alert("请完成登陆")
      window.location.href = "login.html";
      return error ;
    } else if (error.response.status === 403) {
      // 未登录，跳转到登录页面
      //TODO:利用框架来做登陆的跳转
      // router.push('/login');
      alert("身份过期，请重新登录")
      window.location.href = "login.html";
      return error ;
    }else {
      // 其他错误，打印错误信息
      console.error(error);
    }
    return Promise.reject(error);
  });

  //将需要暴露的变量或函数通过返回值进行导出
  return {
    Vue,
    axios
  };
})();
