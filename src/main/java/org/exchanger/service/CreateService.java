package org.exchanger.service;

public interface CreateService<REQ, RES> {
    RES create(REQ request);
}
