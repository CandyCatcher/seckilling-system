package top.candy.seckilling.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import tk.mybatis.mapper.util.StringUtil;
import top.candy.seckilling.result.CodeMsg;
import top.candy.seckilling.result.Result;
import top.candy.seckilling.service.UserService;
import top.candy.seckilling.util.ValidatorUtil;
import top.candy.seckilling.vo.LoginVo;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

@Controller
public class LoginController {
    private static Logger log = LoggerFactory.getLogger(LoginController.class);

    UserService userService;
    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    //不要使用@RestController
    //不能使用@ResponseBody
    @RequestMapping("/to_login")
    public String toLogin() {
        return "/login";
    }

    @RequestMapping("/do_login")
    //要加上这个注解
    @ResponseBody
    public Result<Boolean> doLogin(HttpServletResponse response,  @Valid LoginVo loginVo) {
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
        userService.login(response, loginVo);
        return Result.success(true);
    }

}
