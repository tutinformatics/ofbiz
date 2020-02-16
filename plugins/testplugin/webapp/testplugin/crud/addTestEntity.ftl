<form id="createTestEvent" method="post" action="<@ofbizUrl>createTestEvent</@ofbizUrl>" name="createTestEvent" class="form-horizontal">
<div class="control-group">
    <label class="control-label" for="typeId">${uiLabelMap.TestEntityTypeId}</label>
    <div class="controls">
        <select id="typeId" name="typeId">
            <#list testTypes as testType>
            <option value='${testType.typeId}'>${testType.description}</option>
        </#list>
    </select>
</div>
</div>
<div class="control-group">
<label class="control-label" for="firstName">${uiLabelMap.TestEntityFirstName}</label>
<div class="controls">
    <input type="text" id="firstName" name="firstName" required>
</div>
</div>
<div class="control-group">
<label class="control-label" for="lastName">${uiLabelMap.TestEntityLastName}</label>
<div class="controls">
    <input type="text" id="lastName" name="lastName" required>
</div>
</div>
<div class="control-group">
<label class="control-label" for="comments">${uiLabelMap.TestEntityComment}</label>
<div class="controls">
    <input type="text" id="comments" name="comments">
</div>
</div>
<div class="control-group">
<div class="controls">
    <button type="submit" class="btn">${uiLabelMap.CommonAdd}</button>
</div>
</div>
        </form>