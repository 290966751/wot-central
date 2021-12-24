package com.wot.demo.service;

import org.apache.commons.lang3.tuple.ImmutablePair;

import java.util.List;
import java.util.concurrent.Future;

public interface IImmutablePairService {

    Future<ImmutablePair<Long, String>> queryForObject(Long id);

    List<ImmutablePair<Long, String>> queryForList(List<Long> ids);
}
