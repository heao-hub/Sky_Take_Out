package com.sky.service.impl;

import com.sky.dto.GoodsSalesDTO;
import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.UserMapper;
import com.sky.service.ReportService;
import com.sky.service.WorkSpaceService;
import com.sky.vo.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.util.StringUtil;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

@Service
public class ReportServiceImpl implements ReportService {

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private WorkSpaceService workSpaceService;
    /**
     * 营业额统计
     * @param begin
     * @param end
     * @return
     */
    @Override
    public TurnoverReportVO getTurnoverStstistics(LocalDate begin, LocalDate end) {
        // 创建日期集合
        List<LocalDate> dateList = new ArrayList<>();

        dateList.add(begin);

        while(!begin.equals(end)){
            begin = begin.plusDays(1);
            dateList.add(begin);
        }

        // 创建营业额集合
        List<Double> turnoverList = new ArrayList<>();
        for (LocalDate date : dateList) {
            // 查询date日期营业额数据，营业额是指状态为已完成的订单金额总和
            // select sum(amount) from orders where status = ? and order_time > ? and order_time < ?
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN); // min:当天的0：0：0 0点0分0秒
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);  //max:当天的23：59：59.999

            Map<String, Object> map = new HashMap<>();
            map.put("begin",beginTime);
            map.put("end",endTime);
            map.put("status", Orders.COMPLETED);
            Double turnover = orderMapper.getTurnoverByMap(map);
            // 如果当天没有订单，那么turnover为null，我们需要将其设为0.0
            turnover = turnover==null? 0.0:turnover;
            turnoverList.add(turnover);
        }

        return TurnoverReportVO
                .builder()
                .dateList(StringUtils.join( dateList,","))
                .turnoverList(StringUtils.join(turnoverList,","))
                .build();
    }

    /**
     * 用户统计
     * @param begin
     * @param end
     * @return
     */
    @Override
    public UserReportVO getUserStatistics(LocalDate begin, LocalDate end) {
        // 创建日期列表 存放从begin到end之间的每天对应的日期
        List<LocalDate> dateList = new ArrayList<>();
        dateList.add(begin);
        while(!begin.equals(end)){
            begin = begin.plusDays(1);
            dateList.add(begin);
        }

        // 创建每日用户总数量集合/每日新增用户数量集合
        List<Integer> totalUserList = new ArrayList<>();
        List<Integer> newUserList = new ArrayList<>();

        for (LocalDate date : dateList) {
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);

            Map<String, Object> map = new HashMap<>();

            map.put("end",endTime);
            Integer total = userMapper.getCountByMap(map);

            map.put("begin",beginTime);
            Integer newCount = userMapper.getCountByMap(map);

            totalUserList.add(total);
            newUserList.add(newCount);
        }

        return UserReportVO
                .builder()
                .dateList(StringUtils.join(dateList,","))
                .totalUserList(StringUtils.join(totalUserList,","))
                .newUserList(StringUtils.join(newUserList,","))
                .build();
    }

    /**
     * 订单统计
     * @param begin
     * @param end
     * @return
     */
    @Override
    public OrderReportVO getOrderStatistics(LocalDate begin, LocalDate end) {
        // 创建日期集合
        List<LocalDate> dateList = new ArrayList<>();
        dateList.add(begin);
        while(!begin.equals(end)){
            begin = begin.plusDays(1);
            dateList.add(begin);
        }
        // 创建订单总数集合
        List<Integer> totalCountList = new ArrayList<>();
        // 创建有效订单集合
        List<Integer> completedCountList = new ArrayList<>();
        // 定义总订单数/有效订单数
        Integer totalCount = 0;
        Integer completedCount = 0;
        for (LocalDate date : dateList) {
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);

            Map<String,Object> map = new HashMap<>();
            map.put("begin",beginTime);
            map.put("end",endTime);
            Integer total = orderMapper.getOrderCountByMap(map);
            map.put("status",Orders.COMPLETED);
            Integer completed = orderMapper.getOrderCountByMap(map);

            totalCount += total;
            completedCount += completed;
            totalCountList.add(totalCount);
            completedCountList.add(completed);


        }
        Double orderCompletionRate = totalCount==0?0.0:completedCount.doubleValue() / totalCount.doubleValue();

        return OrderReportVO
                .builder()
                .dateList(StringUtils.join(dateList,","))
                .orderCountList(StringUtils.join(totalCountList,","))
                .totalOrderCount(totalCount)
                .validOrderCountList(StringUtils.join(completedCountList,","))
                .validOrderCount(completedCount)
                .orderCompletionRate(orderCompletionRate)
                .build();
    }

    /**
     * 统计菜品销量前十
     * @param begin
     * @param end
     * @return
     */
    @Override
    public SalesTop10ReportVO getSalesTop10(LocalDate begin, LocalDate end) {
        LocalDateTime beginTime = LocalDateTime.of(begin, LocalTime.MIN);
        LocalDateTime endTime = LocalDateTime.of(end, LocalTime.MAX);

        Map<String ,Object> map = new HashMap<>();
        map.put("begin",beginTime);
        map.put("end",endTime);

        List<GoodsSalesDTO> salesTop10 = orderMapper.getSalesTop10(map);

        List<String> dishNameList = new ArrayList<>();
        List<Integer> dishNumberList = new ArrayList<>();
        for (GoodsSalesDTO good : salesTop10) {
            dishNameList.add(good.getName());
            dishNumberList.add(good.getNumber());
        }


        return SalesTop10ReportVO
                .builder()
                .nameList(StringUtils.join(dishNameList,","))
                .numberList(StringUtils.join(dishNumberList,","))
                .build();
    }

    /**
     * 导出营业数据
     * @param response
     */
    @Override
    public void exportBusinessData(HttpServletResponse response) {
        // 查询数据库，获取营业额数据
        LocalDate date = LocalDate.now();
        LocalDate beginDate = date.minusDays(30);
        LocalDate endDate = date.minusDays(1);
        // 调用workSpaceService 获取近30天的营业数据
        BusinessDataVO totalBusinessData = workSpaceService.getBusinessData(LocalDateTime.of(beginDate, LocalTime.MIN), LocalDateTime.of(endDate, LocalTime.MAX));

        // 通过POI，将数据写到excel文件中
        // 获取输入流
        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("template/运营数据报表模板.xlsx");

        try {
            // 基于提供好的模板创建一个新的excel对象
            XSSFWorkbook excel = new XSSFWorkbook(inputStream);

            // 获取当前文件的第一个sheet
            XSSFSheet sheet = excel.getSheetAt(0);
            // 将时间填到第2行第二列
            XSSFRow row = sheet.getRow(1);
            row.getCell(1).setCellValue("时间："+beginDate.toString()+"至"+endDate.toString());

            // 填写概览数据
            row = sheet.getRow(3);
            row.getCell(2).setCellValue(totalBusinessData.getTurnover());
            row.getCell(4).setCellValue(totalBusinessData.getOrderCompletionRate());
            row.getCell(6).setCellValue(totalBusinessData.getNewUsers());
            row = sheet.getRow(4);
            row.getCell(2).setCellValue(totalBusinessData.getValidOrderCount());
            row.getCell(4).setCellValue(totalBusinessData.getUnitPrice());

            // 填写下面的每日的数据
            for(int i = 0;i < 30;i++){
                LocalDate localDate = beginDate.plusDays(i);
                // 查询当日营业数据
                BusinessDataVO businessData = workSpaceService.getBusinessData(LocalDateTime.of(localDate, LocalTime.MIN), LocalDateTime.of(localDate, LocalTime.MAX));
                row = sheet.getRow(7+i);
                row.getCell(1).setCellValue(localDate.toString());
                row.getCell(2).setCellValue(businessData.getTurnover());
                row.getCell(3).setCellValue(businessData.getValidOrderCount());
                row.getCell(4).setCellValue(businessData.getOrderCompletionRate());
                row.getCell(5).setCellValue(businessData.getUnitPrice());
                row.getCell(6).setCellValue(businessData.getNewUsers());
            }

            // 将写好的文件通过输出流下载到客户端浏览器
            ServletOutputStream outputStream = response.getOutputStream();
            excel.write(outputStream);
            // 关闭资源
            outputStream.flush();
            excel.close();
            inputStream.close();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }
}
