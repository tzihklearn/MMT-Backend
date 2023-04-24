package com.sipc.mmtbackend.iService;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.sipc.mmtbackend.pojo.domain.Tag;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Map;
import java.util.function.Function;

/**
 * @author tzih
 * @version v1.0
 * @since 2023.04.23
 */
@Component
public class TagIService implements IService<Tag> {

    @Override
    public boolean saveBatch(Collection<Tag> entityList, int batchSize) {
        return false;
    }

    @Override
    public boolean saveOrUpdateBatch(Collection<Tag> entityList, int batchSize) {
        return false;
    }

    @Override
    public boolean updateBatchById(Collection<Tag> entityList, int batchSize) {
        return false;
    }

    @Override
    public boolean saveOrUpdate(Tag entity) {
        return false;
    }

    @Override
    public Tag getOne(Wrapper<Tag> queryWrapper, boolean throwEx) {
        return null;
    }

    @Override
    public Map<String, Object> getMap(Wrapper<Tag> queryWrapper) {
        return null;
    }

    @Override
    public <V> V getObj(Wrapper<Tag> queryWrapper, Function<? super Object, V> mapper) {
        return null;
    }

    @Override
    public BaseMapper<Tag> getBaseMapper() {
        return null;
    }

    @Override
    public Class<Tag> getEntityClass() {
        return null;
    }
}
