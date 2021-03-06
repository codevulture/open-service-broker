package com.swisscom.cloud.sb.broker.provisioning.lastoperation

import com.swisscom.cloud.sb.broker.BaseTransactionalSpecification
import com.swisscom.cloud.sb.broker.model.LastOperation
import com.swisscom.cloud.sb.broker.model.repository.LastOperationRepository
import com.swisscom.cloud.sb.broker.util.DBTestUtil
import com.swisscom.cloud.sb.broker.util.StringGenerator
import org.springframework.beans.factory.annotation.Autowired

class LastOperationJobContextSpec extends BaseTransactionalSpecification {
    private String id = StringGenerator.randomUuid()
    @Autowired
    DBTestUtil dbTestUtil
    @Autowired
    LastOperationRepository lastOperationRepository
    @Autowired
    LastOperationJobContextService lastOperationJobContextService

    def "last operation state and description should be set correctly"() {
        given:
        LastOperationJobContext lastOperationContext = createLastOperationContext()
        when:
        lastOperationContext.notifyResult(success, description)
        then:
        def lastOperation = lastOperationRepository.findByGuid(id)
        lastOperation.status == success ? LastOperation.Status.SUCCESS : LastOperation.Status.FAILED
        lastOperation.description == description
        where:
        success | description
        true    | 'desc1'
        false   | 'desc2'
    }

    def "NotifySuccess works correctly"() {
        given:
        LastOperationJobContext lastOperationContext = createLastOperationContext()
        when:
        lastOperationContext.notifySuccess("blabla")
        then:
        def lastOperation = lastOperationRepository.findByGuid(id)
        lastOperation.status == LastOperation.Status.SUCCESS
        lastOperation.description == 'blabla'
    }

    def "NotifyFailure works correctly"() {
        given:
        LastOperationJobContext lastOperationContext = createLastOperationContext()
        when:
        lastOperationContext.notifyFailure("blabla")
        then:
        def lastOperation = lastOperationRepository.findByGuid(id)
        lastOperation.status == LastOperation.Status.FAILED
        lastOperation.description == 'blabla'
    }

    private LastOperationJobContext createLastOperationContext() {
        dbTestUtil.createLastOperation(id)
        lastOperationJobContextService.loadContext(id)
    }
}
