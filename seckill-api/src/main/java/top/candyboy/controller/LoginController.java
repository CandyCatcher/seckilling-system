package top.candyboy.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import top.candyboy.result.CodeMsg;
import top.candyboy.result.Result;
import top.candyboy.service.UserService;
import top.candyboy.pojo.vo.LoginVo;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

@RestController
public class LoginController {
    private static Logger log = LoggerFactory.getLogger(LoginController.class);

    UserService userService;
    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    //不要使用@RestController
    //不能使用@ResponseBody
    @PostMapping("/tologin")
    public String toLogin() {
        return "/login";
    }

    @PostMapping("/dologin")
    //要加上这个注解
    @ResponseBody
    public Result<Boolean> doLogin(HttpServletResponse response, @Valid LoginVo loginVo) {
    //public Result<Boolean> doLogin(LoginVo loginVo) {

        log.info(loginVo.toString());
        //参数校验
        //每次都要这么一堆参数校验
        /*
            String mobile = loginVo.getMobile();
            String password = loginVo.getPassword();
            if (StringUtil.isEmpty(password)) {
                return Result.error(CodeMsg.PASSWORD_EMPTY);
            }
            if (StringUtil.isEmpty(mobile)) {
                return Result.error(CodeMsg.MOBILE_EMPTY);
            }
            if (!ValidatorUtil.isMobile(mobile)) {
                return Result.error(CodeMsg.MOBILE_ERROR);
            }
        */
        //因为异常都抛出去了，这里就不用分类了
        //CodeMsg codeMsg = userService.login(loginVo);
        /*
            if (codeMsg.getCode() == 0) {
                return Result.success(true);
            } else {
                return Result.error(codeMsg);
            }
        */

        return userService.login(response, loginVo);
    }

}
