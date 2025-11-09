package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.context.BaseContext;
import com.sky.dto.EmployeeDTO;
import com.sky.dto.EmployeeLoginDTO;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.dto.PasswordEditDTO;
import com.sky.entity.Employee;
import com.sky.exception.AccountLockedException;
import com.sky.exception.AccountNotFoundException;
import com.sky.exception.PasswordErrorException;
import com.sky.mapper.EmployeeMapper;
import com.sky.result.PageResult;
import com.sky.service.EmployeeService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.LocalDataSourceJobStore;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    @Autowired
    private EmployeeMapper employeeMapper;

    /**
     * 员工登录
     *
     * @param employeeLoginDTO
     * @return
     */
    public Employee login(EmployeeLoginDTO employeeLoginDTO) {
        String username = employeeLoginDTO.getUsername();
        String password = employeeLoginDTO.getPassword();

        //1、根据用户名查询数据库中的数据
        Employee employee = employeeMapper.getByUsername(username);

        //2、处理各种异常情况（用户名不存在、密码不对、账号被锁定）
        if (employee == null) {
            //账号不存在
            throw new AccountNotFoundException(MessageConstant.ACCOUNT_NOT_FOUND);
        }

        //密码比对  数据库中存储的密码是经过md5算法加密过的
        //对前端传来的密码进行md5加密，然后再进行比对数据库中存储的进行比对
        password = DigestUtils.md5DigestAsHex(password.getBytes());
        if (!password.equals(employee.getPassword())) {
            //密码错误
            throw new PasswordErrorException(MessageConstant.PASSWORD_ERROR);
        }

        if (employee.getStatus() == StatusConstant.DISABLE) {
            //账号被锁定
            throw new AccountLockedException(MessageConstant.ACCOUNT_LOCKED);
        }

        //3、返回实体对象
        return employee;
    }

    /**
     * 新增员工
     * @param employeeDTO
     */
    public void save(EmployeeDTO employeeDTO){
        Employee employee = new Employee();

        //将dto对象中的属性拷贝到employee中
        BeanUtils.copyProperties(employeeDTO,employee);

        //设置账号的状态，默认正常状态 1 表示正常 0表示禁用
        employee.setStatus(StatusConstant.ENABLE);

        //设置密码，默认密码为123456，要将原始密码经过md5加密后的值存入数据库中
        String password = DigestUtils.md5DigestAsHex("123456".getBytes());
        employee.setPassword(password);

        /*//设置创建时间
        employee.setCreateTime(LocalDateTime.now());

        //设置修改时间
        employee.setUpdateTime(LocalDateTime.now());

        //设置当前记录创建人id和修改人id
        employee.setCreateUser(BaseContext.getCurrentId());
        employee.setUpdateUser(BaseContext.getCurrentId());*/

        employeeMapper.insert(employee);


    }

    /**
     * 分页查询员工
     * @param employeePageQueryDTO
     * @return
     */
    public PageResult pageQuery(EmployeePageQueryDTO employeePageQueryDTO) {
        //开始分页查询
        PageHelper.startPage(employeePageQueryDTO.getPage(),employeePageQueryDTO.getPageSize());

        Page<Employee> result = employeeMapper.pageQuery(employeePageQueryDTO);
        long total = result.getTotal();
        List<Employee> records = result.getResult();


        return new PageResult(total,records);
    }

    /**
     * 启用禁用员工账号
     * @param status
     * @param id
     */
    @Override
    public void startOrStop(Integer status, Long id) {
        Employee employee = Employee.builder()
                .id(id)
                .status(status)
                .build();

        employeeMapper.update(employee);
    }

    /**
     * 根据id查询员工信息
     * @param id
     * @return
     */
    @Override
    public Employee getById(Long id) {

        Employee emp = employeeMapper.selectById(id);
        //不显示密码
        emp.setPassword("****");

        return emp;
    }

    /**
     * 修改员工信息
     * @param employeeDTO
     */
    @Override
    public void updateEmp(EmployeeDTO employeeDTO) {
        Employee employee = new Employee();
        BeanUtils.copyProperties(employeeDTO,employee);
       /* employee.setUpdateUser(BaseContext.getCurrentId());
        employee.setUpdateTime(LocalDateTime.now());*/
        employeeMapper.update(employee);

    }

    /**
     * 修改密码
     * @param passwordEditDTO
     */

    @Override
    public void updatePassword(PasswordEditDTO passwordEditDTO) {

        Employee employee = employeeMapper.selectById(passwordEditDTO.getEmpId());

        if(employee == null){
            //账号不存在
            throw new AccountLockedException(MessageConstant.ACCOUNT_NOT_FOUND);
        }

        String oldPassword = passwordEditDTO.getOldPassword();
        oldPassword = DigestUtils.md5DigestAsHex(oldPassword.getBytes());
        if(!oldPassword.equals(employee.getPassword())){
            throw new PasswordErrorException(MessageConstant.PASSWORD_ERROR);
        }else{
            String newPassword = DigestUtils.md5DigestAsHex(passwordEditDTO.getNewPassword().getBytes());
            employee.setPassword(newPassword);
            /*employee.setUpdateTime(LocalDateTime.now());
            employee.setUpdateUser(BaseContext.getCurrentId());*/
            employeeMapper.update(employee);
        }
    }

}
