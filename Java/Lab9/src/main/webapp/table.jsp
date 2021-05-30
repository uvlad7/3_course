<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<table class="table table-striped table-dark table-bordered" id="main_table">
    <jsp:useBean id="recs" class="app.entity.RecordList" scope="request"/>
    <thead>
    <tr>
        <th scope="col">Word</th>
        <c:choose>
            <c:when test="${'T'.equals(recs.type)}">
                <th scope="col">Translation</th>
            </c:when>
            <c:when test="${'E'.equals(recs.type)}">
                <th scope="col" colspan="2">Explanation</th>
            </c:when>
            <c:otherwise>
                <th scope="col" colspan="2">Definition</th>
                <th scope="col" colspan="2">Vocabulary</th>
            </c:otherwise>
        </c:choose>
    </tr>
    </thead>
    <tbody>
    <c:choose>
        <c:when test="${empty recs.records}">
            <tr>
                <td colspan="5">
                    <div class="word">Nothing selected</div>
                </td>
            </tr>
        </c:when>
        <c:otherwise>
            <c:forEach items="${recs.records}" var="rec">
                <tr class="clickable-row" data-word_id="${rec.word.id}" data-record_id="${rec.id}"
                    data-is_exp="${rec.explanatory}">
                    <td>
                        <div class="word" id="word_${rec.id}_${rec.explanatory}">${rec.word}</div>
                    </td>
                    <c:choose>
                        <c:when test="${'T'.equals(recs.type)}">
                            <td>
                                <div class="word" id="word2_${rec.id}_${rec.explanatory}">${rec.word2}</div>
                            </td>
                        </c:when>
                        <c:when test="${'E'.equals(recs.type)}">
                            <td colspan="2">
                                <div class="word" id="word2_${rec.id}_${rec.explanatory}">${rec.word2}</div>
                            </td>
                        </c:when>
                        <c:otherwise>
                            <td colspan="2">
                                <div class="word" id="word2_${rec.id}_${rec.explanatory}">${rec.word2}</div>
                            </td>
                            <td colspan="2">
                                <div class="word">${rec.vocabulary}</div>
                            </td>
                        </c:otherwise>
                    </c:choose>
                </tr>
            </c:forEach>
        </c:otherwise>
    </c:choose>
    </tbody>
</table>
