/*
 * Salesforce DTO generated by camel-salesforce-maven-plugin
 * Generated on: Mon Mar 02 02:58:34 EST 2015
 */
package org.apache.camel.salesforce.dto;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamConverter;
import org.codehaus.jackson.annotate.JsonProperty;
import org.apache.camel.component.salesforce.api.PicklistEnumConverter;
import org.apache.camel.component.salesforce.api.dto.AbstractSObjectBase;

/**
 * Salesforce DTO for SObject DashboardComponentFeed
 */
@XStreamAlias("DashboardComponentFeed")
public class DashboardComponentFeed extends AbstractSObjectBase {

    // ParentId
    private String ParentId;

    @JsonProperty("ParentId")
    public String getParentId() {
        return this.ParentId;
    }

    @JsonProperty("ParentId")
    public void setParentId(String ParentId) {
        this.ParentId = ParentId;
    }

    // Type
    @XStreamConverter(PicklistEnumConverter.class)
    private TypeEnum Type;

    @JsonProperty("Type")
    public TypeEnum getType() {
        return this.Type;
    }

    @JsonProperty("Type")
    public void setType(TypeEnum Type) {
        this.Type = Type;
    }

    // CommentCount
    private Integer CommentCount;

    @JsonProperty("CommentCount")
    public Integer getCommentCount() {
        return this.CommentCount;
    }

    @JsonProperty("CommentCount")
    public void setCommentCount(Integer CommentCount) {
        this.CommentCount = CommentCount;
    }

    // LikeCount
    private Integer LikeCount;

    @JsonProperty("LikeCount")
    public Integer getLikeCount() {
        return this.LikeCount;
    }

    @JsonProperty("LikeCount")
    public void setLikeCount(Integer LikeCount) {
        this.LikeCount = LikeCount;
    }

    // Title
    private String Title;

    @JsonProperty("Title")
    public String getTitle() {
        return this.Title;
    }

    @JsonProperty("Title")
    public void setTitle(String Title) {
        this.Title = Title;
    }

    // Body
    private String Body;

    @JsonProperty("Body")
    public String getBody() {
        return this.Body;
    }

    @JsonProperty("Body")
    public void setBody(String Body) {
        this.Body = Body;
    }

    // LinkUrl
    private String LinkUrl;

    @JsonProperty("LinkUrl")
    public String getLinkUrl() {
        return this.LinkUrl;
    }

    @JsonProperty("LinkUrl")
    public void setLinkUrl(String LinkUrl) {
        this.LinkUrl = LinkUrl;
    }

    // RelatedRecordId
    private String RelatedRecordId;

    @JsonProperty("RelatedRecordId")
    public String getRelatedRecordId() {
        return this.RelatedRecordId;
    }

    @JsonProperty("RelatedRecordId")
    public void setRelatedRecordId(String RelatedRecordId) {
        this.RelatedRecordId = RelatedRecordId;
    }

    // ContentData
    // blob field url, use getBlobField to get the content
    @XStreamAlias("ContentData")
    private String ContentDataUrl;

    @JsonProperty("ContentData")
    public String getContentDataUrl() {
        return this.ContentDataUrl;
    }

    @JsonProperty("ContentData")
    public void setContentDataUrl(String ContentDataUrl) {
        this.ContentDataUrl = ContentDataUrl;
    }

    // ContentFileName
    private String ContentFileName;

    @JsonProperty("ContentFileName")
    public String getContentFileName() {
        return this.ContentFileName;
    }

    @JsonProperty("ContentFileName")
    public void setContentFileName(String ContentFileName) {
        this.ContentFileName = ContentFileName;
    }

    // ContentDescription
    private String ContentDescription;

    @JsonProperty("ContentDescription")
    public String getContentDescription() {
        return this.ContentDescription;
    }

    @JsonProperty("ContentDescription")
    public void setContentDescription(String ContentDescription) {
        this.ContentDescription = ContentDescription;
    }

    // ContentType
    private String ContentType;

    @JsonProperty("ContentType")
    public String getContentType() {
        return this.ContentType;
    }

    @JsonProperty("ContentType")
    public void setContentType(String ContentType) {
        this.ContentType = ContentType;
    }

    // ContentSize
    private Integer ContentSize;

    @JsonProperty("ContentSize")
    public Integer getContentSize() {
        return this.ContentSize;
    }

    @JsonProperty("ContentSize")
    public void setContentSize(Integer ContentSize) {
        this.ContentSize = ContentSize;
    }

    // InsertedById
    private String InsertedById;

    @JsonProperty("InsertedById")
    public String getInsertedById() {
        return this.InsertedById;
    }

    @JsonProperty("InsertedById")
    public void setInsertedById(String InsertedById) {
        this.InsertedById = InsertedById;
    }

}
