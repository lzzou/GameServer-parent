package com.base.component;

import com.alibaba.fastjson.JSON;
import com.base.config.toml.AllConfig;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.tomlj.JsonOptions;
import org.tomlj.Toml;
import org.tomlj.TomlParseResult;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Objects;

/**
 * 全局配置组件
 *
 * @author zlz
 */
@Component
@Slf4j
public class GlobalConfigComponent extends AbstractComponent {

    private static final String CONFIG_FILE = "bootstrap.toml";

    @Getter
    private static AllConfig config;

    private static boolean init(InputStream stream) {
        try {
            // xml配置
            // String xmlStr = FileUtil.readTxt(path, "UTF-8");
            // config = XmlUtil.toObject(xmlStr, AllConfigList.class);
            TomlParseResult toml = Toml.parse(stream);
            config = JSON.parseObject(toml.toJson(JsonOptions.ALL_VALUES_AS_STRINGS), AllConfig.class);
            if (config == null) {
                log.error("Config file Load Failed.");
                return false;
            }
            log.info("config load complete, config: {}", JSON.toJSONString(config));
            return true;
        } catch (IOException e) {
            log.error(e.getMessage());
        }
        return false;
    }

    @Override
    public boolean initialize() {
        InputStream stream = getClass().getClassLoader().getResourceAsStream(CONFIG_FILE);
        if (Objects.nonNull(stream)) {
            return init(stream);
        } else {
            log.error("Can not find the global config file path, config file : {}", CONFIG_FILE);
            return false;
        }
    }

    @Override
    public void stop() {
        config = null;
    }
}
