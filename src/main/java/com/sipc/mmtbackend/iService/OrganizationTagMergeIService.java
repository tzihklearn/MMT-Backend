package com.sipc.mmtbackend.iService;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.sipc.mmtbackend.pojo.domain.OrganizationTagMerge;
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
public class OrganizationTagMergeIService implements IService<OrganizationTagMerge> {

    @Override
    public boolean saveBatch(Collection<OrganizationTagMerge> entityList, int batchSize) {
        return false;
    }

    @Override
    public boolean saveOrUpdateBatch(Collection<OrganizationTagMerge> entityList, int batchSize) {
        return false;
    }

    @Override
    public boolean updateBatchById(Collection<OrganizationTagMerge> entityList, int batchSize) {
        return false;
    }

    @Override
    public boolean saveOrUpdate(OrganizationTagMerge entity) {
        return false;
    }

    @Override
    public OrganizationTagMerge getOne(Wrapper<OrganizationTagMerge> queryWrapper, boolean throwEx) {
        return null;
    }

    @Override
    public Map<String, Object> getMap(Wrapper<OrganizationTagMerge> queryWrapper) {
        return null;
    }

    @Override
    public <V> V getObj(Wrapper<OrganizationTagMerge> queryWrapper, Function<? super Object, V> mapper) {
        return null;
    }

    @Override
    public BaseMapper<OrganizationTagMerge> getBaseMapper() {
        return null;
    }

    @Override
    public Class<OrganizationTagMerge> getEntityClass() {
        return null;
    }
}
