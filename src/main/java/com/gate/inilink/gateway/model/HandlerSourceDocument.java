package com.gate.inilink.gateway.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "handler")
public class HandlerSourceDocument {

    @Id
    private String id;
    private String handlerType;
    private String className;
    private String beanName;
    private String sourceCode;
    private boolean enabled;
}
