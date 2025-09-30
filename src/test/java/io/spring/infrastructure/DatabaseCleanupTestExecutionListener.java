package io.spring.infrastructure;

import java.util.List;
import javax.sql.DataSource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.TestExecutionListener;

public class DatabaseCleanupTestExecutionListener implements TestExecutionListener {

  @Override
  public void beforeTestMethod(TestContext testContext) throws Exception {
    cleanDatabase(testContext);
  }

  private void cleanDatabase(TestContext testContext) {
    DataSource dataSource = testContext.getApplicationContext().getBean(DataSource.class);
    JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);

    // Get all table names except Flyway migration tables
    List<String> tableNames =
        jdbcTemplate.queryForList(
            "SELECT table_name FROM information_schema.tables WHERE table_schema = 'public' "
                + "AND table_name NOT LIKE 'flyway_%' "
                + "AND table_type = 'BASE TABLE'",
            String.class);

    if (!tableNames.isEmpty()) {
      // Disable foreign key constraints temporarily
      jdbcTemplate.execute("SET session_replication_role = 'replica'");

      // Truncate all tables
      for (String tableName : tableNames) {
        jdbcTemplate.execute("TRUNCATE TABLE " + tableName + " CASCADE");
      }

      // Re-enable foreign key constraints
      jdbcTemplate.execute("SET session_replication_role = 'origin'");
    }
  }
}
