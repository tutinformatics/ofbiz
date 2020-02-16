<div>
    <#if TestEntityList?has_content>
    <table cellspacing=0 cellpadding=2 border=0 class="table table-bordered table-striped table-hover">
    <thead><tr>
        <th>${uiLabelMap.TestEntityId}</th>
        <th>${uiLabelMap.TestEntityType}</th>
        <th>${uiLabelMap.TestEntityFirstName}</th>
        <th>${uiLabelMap.TestEntityLastName}</th>
        <th>${uiLabelMap.TestEntityComment}</th>
    </tr></thead>
    <tbody>
    <#list TestEntityList as testEntity>
    <tr>
        <td>${testEntity.id}</td>
        <td>${testEntity.getRelatedOne("testType").get("description", locale)}</td>
        <td>${testEntity.firstName?default("NA")}</td>
        <td>${testEntity.lastName?default("NA")}</td>
        <td>${testEntity.comments!}</td>
    </tr>
    </#list>
</tbody>
        </table>
        </#if>
        </div>