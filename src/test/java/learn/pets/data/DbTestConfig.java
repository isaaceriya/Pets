package learn.pets.data;

import com.mysql.cj.jdbc.MysqlDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

@ComponentScan
public class DbTestConfig {

    @Bean
    public DataSource getDataSource() {
        MysqlDataSource result = new MysqlDataSource();

        result.setUrl("jdbc:mysql://localhost:3306/pets_test");
        result.setUser("root");
        result.setPassword("top-secret-password");

        return result;
    }

    @Bean
    public JdbcTemplate getJdbcTemplate(DataSource datasource) {
        return new JdbcTemplate(datasource);
    }

}
