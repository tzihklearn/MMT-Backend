package com.sipc.mmtbackend.pojo.c.domain;

public class RegistrationForm {
    private Integer id;

    private Integer admissionId;

    private Integer questionOrder;

    private String question;

    private String type;

    private Integer remark;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getAdmissionId() {
        return admissionId;
    }

    public void setAdmissionId(Integer admissionId) {
        this.admissionId = admissionId;
    }

    public Integer getQuestionOrder() {
        return questionOrder;
    }

    public void setQuestionOrder(Integer questionOrder) {
        this.questionOrder = questionOrder;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getRemark() {
        return remark;
    }

    public void setRemark(Integer remark) {
        this.remark = remark;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", organizationId=").append(admissionId);
        sb.append(", questionOrder=").append(questionOrder);
        sb.append(", question=").append(question);
        sb.append(", type=").append(type);
        sb.append(", remark=").append(remark);
        sb.append("]");
        return sb.toString();
    }
}