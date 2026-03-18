package auto;

import com.baomidou.mybatisplus.generator.AutoGenerator;
import com.baomidou.mybatisplus.generator.config.DataSourceConfig;
import com.baomidou.mybatisplus.generator.config.GlobalConfig;
import com.baomidou.mybatisplus.generator.config.PackageConfig;
import com.baomidou.mybatisplus.generator.config.StrategyConfig;
import com.baomidou.mybatisplus.generator.config.rules.DbType;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;

import java.io.File;

/**
 * @author zengry
 * @description
 * @since 2020/3/5
 */
public class CodeAutoGenerator {

    public static void main(String[] args) {
        //1. 全局配置
        GlobalConfig config = new GlobalConfig();
        // 是否支持AR模式
        config.setActiveRecord(false);
        // 作者
        config.setAuthor("chenm");
        // 生成路径
        config.setOutputDir(new File("license-service/src/main/java").getAbsolutePath());
        // 文件覆盖
        config.setFileOverride(true);
        // 设置生成的service接口的名字的首字母是否为I
        config.setServiceName("%sService");
        // 否打开输出目录弹窗
        config.setOpen(false);
        config.setBaseResultMap(true);
        config.setBaseColumnList(true);
        // XML 二级缓存
        config.setEnableCache(false);

        //2. 数据源配置
        DataSourceConfig dsConfig = new DataSourceConfig();
        // 设置数据库类型
        dsConfig.setDbType(DbType.MYSQL);
        dsConfig.setDriverName("com.mysql.jdbc.Driver");
        dsConfig.setUrl(
                "jdbc:mysql://192.168.101.2:3306/simba_quality?useSSL=true&verifyServerCertificate=false&allowMultiQueries=true&useUnicode=true&characterEncoding=UTF-8");
        dsConfig.setUsername("root");
        dsConfig.setPassword("Hk3332222@");

        //3. 策略配置
        StrategyConfig stConfig = new StrategyConfig();
        //全局大写命名
        stConfig.setCapitalMode(true);
        // 指定表名 字段名是否使用下划线
        stConfig.setDbColumnUnderline(true);
        // 数据库表映射到实体的命名策略
        stConfig.setDbColumnUnderline(true);
        stConfig.setNaming(NamingStrategy.underline_to_camel);
        //        stConfig.setTablePrefix(new String[] { "contract_route" });
        // 需要生成的表
        stConfig.setInclude(new String[]{"violation_type_process_mode"});

        //4. 包名策略配置
        PackageConfig pkConfig = new PackageConfig();
        pkConfig.setParent("com.hk.simba.license.service");
        pkConfig.setMapper("mapper");
        pkConfig.setService("service");
        pkConfig.setEntity("entity");
        pkConfig.setXml("mapper");

        //5. 整合配置
        AutoGenerator ag = new AutoGenerator();
        ag.setGlobalConfig(config);
        ag.setDataSource(dsConfig);
        ag.setStrategy(stConfig);
        ag.setPackageInfo(pkConfig);

        //6. 执行
        ag.execute();
    }

}
