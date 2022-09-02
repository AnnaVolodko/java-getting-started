/*
 * Copyright 2002-2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Map;

@Controller
@SpringBootApplication
public class Main {

  @Value("${spring.datasource.url}")
  private String dbUrl;

  @Autowired
  private DataSource dataSource;

  public static void main(String[] args) throws Exception {
    SpringApplication.run(Main.class, args);
  }

  @RequestMapping("/")
  String index() {
    return "index";
  }

  @RequestMapping("/db")
  String db(Map<String, Object> model) {
    try (Connection connection = dataSource.getConnection()) {
      Statement stmt = connection.createStatement();
      stmt.executeUpdate("CREATE TABLE IF NOT EXISTS ticks (tick timestamp)");
      stmt.executeUpdate("INSERT INTO ticks VALUES (now())");
      ResultSet rs = stmt.executeQuery("SELECT tick FROM ticks");

      ArrayList<String> output = new ArrayList<String>();
      while (rs.next()) {
        output.add("Read from DB: " + rs.getTimestamp("tick"));
      }

      model.put("records", output);
      return "db";
    } catch (Exception e) {
      model.put("message", e.getMessage());
      return "error";
    }
  }

  // @RequestMapping("/RuleEngineRecords")
  // String dbAxs(Map<String, Object> model) {
  //   try (Connection connection = dataSource.getConnection()) {
  //     Statement stmt = connection.createStatement();
  //     stmt.executeUpdate("CREATE TABLE IF NOT EXISTS AXSrecs (tick timestamp)");
  //     stmt.executeUpdate("INSERT INTO AXSrecs VALUES (now())");
  //     ResultSet rs = stmt.executeQuery("SELECT tick FROM AXSrecs");

  //     ArrayList<String> output = new ArrayList<String>();
  //     while (rs.next()) {
  //       output.add("Read from DB: " + rs.getTimestamp("tick"));
  //     }

  //     model.put("records", output);
  //     return "dbAXS";
  //   } catch (Exception e) {
  //     model.put("message", e.getMessage());
  //     return "error";
  //   }
  // }

  ///////////////////////

  //   @RequestMapping("/process")
  //   public String processSettlement1(Map<String, Object> model) {
        
  //       ArrayList<String> output = new ArrayList<String>();   
  //       ArrayList<String> outputmessage = new ArrayList<String>();

  //       try (Connection connection = dataSource.getConnection()) {
  //         Statement stmt = connection.createStatement();    
  //         String transid="";
  //         ResultSet rs = stmt.executeQuery("SELECT  * FROM Salesforce.AXS_POC__c  WHERE  (processed_yn__c is null or processed_yn__c = 'f') LIMIT 2");
  //         while (rs.next()) {  

  //           transid = rs.getString("Transaction_id__c");
            
  //         }
        
  //       if (transid !="") {
  //         stmt.executeUpdate("UPDATE Salesforce.AXS_POC__c set processed_yn__c = 'Yes' where Transaction_id__c = '" + transid + "'");   
  //         outputmessage.add("Settlement Process Fired ");              
  //        }  
  //       outputmessage.add("Settlement Process Completed");   
  //       model.put("message", outputmessage);
          
        
  //       System.out.println("checkpoint2");   
  //       ResultSet rs1 = stmt.executeQuery("select  Transaction_id__c,ordernumber__c,Component__c,fee__c,processed_yn__c from Salesforce.AXS_POC__c ");              
        
  //       while (rs1.next()) {   
  //         output.add("ordernumber : " + rs1.getString("ordernumber__c"));
  //         output.add("Component : " + rs1.getString("Component__c"));
  //         output.add("fee : " + rs1.getString("fee__c"));
  //         output.add("Transaction_id : " + rs1.getString("Transaction_id__c"));
  //         output.add("processed_yn : " + rs1.getString("processed_yn__c"));
  //         output.add("-----------------------------------------------");
  //         }
        
  //       model.put("records", output);
  //       return "db";
  //       } catch (Exception e) {
  //       model.put("message", e.getMessage());
  //       return "error";
  //       }
  //   }

  @RequestMapping("/hello")
  String hello(Map<String, Object> model) {
    model.put("message", "Welcome to my app!");
    return "hello";
  }

  @Bean
  public DataSource dataSource() throws SQLException {
    if (dbUrl == null || dbUrl.isEmpty()) {
      return new HikariDataSource();
    } else {
      HikariConfig config = new HikariConfig();
      config.setJdbcUrl(dbUrl);
      return new HikariDataSource(config);
    }
  }

}
