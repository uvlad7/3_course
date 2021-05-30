<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<header>
    <ul class="nav navbar-nav">
        <li class="dropdown">
            <a class="dropdown-toggle" data-toggle="dropdown" role="button"
               aria-haspopup="true" aria-expanded="true"> <span
                    class="nav-label">Vocabularies</span> <span class="caret"></span></a>
            <ul class="dropdown-menu">
                <jsp:useBean id="vocs" class="app.entity.VocabularyList" scope="request"/>
                <c:forEach items="${vocs.vocabularies}" var="voc">
                    <li><a id="avoc_${voc.id}"
                           href="?id=${voc.id}&explanatory=${voc.explanatory}">${voc}</a></li>
                </c:forEach>
            </ul>
        </li>
        <li class="dropdown">
            <a class="dropdown-toggle" data-toggle="dropdown" role="button"
               aria-haspopup="true" aria-expanded="true"> <span
                    class="nav-label">Editor</span> <span class="caret"></span></a>
            <ul class="dropdown-menu">
                <li><a id="add_vocabulary">Add Vocabulary</a></li>
                <li><a id="add_record">Add Record</a></li>
                <li><a id="add_word">Add Word</a></li>
                <li role='separator' class='divider'></li>
                <li><a id="edit_vocabulary">Edit Vocabulary</a></li>
                <li><a id="edit_record">Edit Record</a></li>
                <li><a id="edit_word">Edit Word</a></li>
                <li role='separator' class='divider'></li>
                <li><a id="delete_vocabulary">Delete Vocabulary</a></li>
                <li><a id="delete_record">Delete Record</a></li>
                <li><a id="delete_word">Delete Word</a></li>
            </ul>
        </li>
        <li><a data-toggle="modal" data-target="#findWord">Find Word</a></li>
    </ul>
</header>

<div class="modal fade" id="findWord" tabindex="-1" role="dialog" aria-labelledby="findWordTitle"
     aria-hidden="true">
    <div class="modal-dialog modal-dialog-centered" role="document">
        <div class="modal-content">
            <div class="modal-header">
                <h4 class="modal-title" id="findWordTitle">Find word</h4>
                <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                    <span aria-hidden="true">&times;</span>
                </button>
            </div>
            <form id="inputSubstrForm" method="get">
                <div class="modal-body">
                    <div class="form-group row">
                        <label for="like_str" class="col-sm-2 col-form-label">Substring</label>
                        <div class="col-sm-10">
                            <input type="text" class="form-control" id="like_str" name="like_str">
                        </div>
                    </div>
                </div>
            </form>
            <div class="modal-footer">
                <button type="submit" class="btn btn-primary" form="inputSubstrForm">OK</button>
                <button type="reset" class="btn btn-secondary" data-dismiss="modal"
                        form="inputSubstrForm">Cancel
                </button>
            </div>
        </div>
    </div>
</div>
<div class="modal fade" id="recordNotSelected" tabindex="-1" role="dialog"
     aria-labelledby="recordNotSelectedTitle"
     aria-hidden="true">
    <div class="modal-dialog modal-dialog-centered" role="document">
        <div class="modal-content">
            <div class="modal-header">
                <h4 class="modal-title" id="recordNotSelectedTitle">Record not selected</h4>
                <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                    <span aria-hidden="true">&times;</span>
                </button>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-secondary" data-dismiss="modal">OK</button>
            </div>
        </div>
    </div>
</div>
<div class="modal fade" id="editVoc" tabindex="-1" role="dialog" aria-labelledby="editVocTitle"
     aria-hidden="true">
    <div class="modal-dialog modal-dialog-centered" role="document">
        <div class="modal-content">
            <div class="modal-header">
                <h4 class="modal-title" id="editVocTitle">Edit vocabulary</h4>
                <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                    <span aria-hidden="true">&times;</span>
                </button>
            </div>
            <div class="modal-body">
                <div class="form-group row">
                    <form id="editVocForm" method="post">
                        <input type="hidden" id="edit_voc_id" name="voc_id">
                        <input type="hidden" id="edit_action_name" name="action_name">
                        <label for="voc_name" class="col-sm-2 col-form-label">Name</label>
                        <div class="col-sm-10">
                            <input type="text" required="required" class="form-control"
                                   id="voc_name" name="voc_name">
                        </div>
                        <label class="form-check-label" for="edit_voc_exp">
                            Explanatory
                        </label>
                        <input class="form-check-input" type="checkbox" id="edit_voc_exp"
                               name="voc_exp">
                    </form>
                </div>
            </div>
            <div class="modal-footer">
                <button type="submit" class="btn btn-primary" form="editVocForm">OK</button>
                <button type="reset" class="btn btn-secondary" data-dismiss="modal"
                        form="editVocForm">Cancel
                </button>
            </div>
        </div>
    </div>
</div>
<div class="modal fade" id="editWord" tabindex="-1" role="dialog" aria-labelledby="editWordTitle"
     aria-hidden="true">
    <div class="modal-dialog modal-dialog-centered" role="document">
        <div class="modal-content">
            <div class="modal-header">
                <h4 class="modal-title" id="editWordTitle">Edit word</h4>
                <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                    <span aria-hidden="true">&times;</span>
                </button>
            </div>
            <div class="modal-body">
                <div class="form-group row">
                    <form id="editWordForm" method="post">
                        <input type="hidden" id="edit_word_id" name="word_id">
                        <input type="hidden" id="word_action_name" name="action_name">
                        <label for="word_text" class="col-sm-2 col-form-label">Word</label>
                        <div class="col-sm-10">
                            <input required="required" type="text" class="form-control"
                                   id="word_text" name="word_text">
                        </div>
                    </form>
                </div>
            </div>
            <div class="modal-footer">
                <button type="submit" class="btn btn-primary" form="editWordForm">OK</button>
                <button type="reset" class="btn btn-secondary" data-dismiss="modal"
                        form="editWordForm">Cancel
                </button>
            </div>
        </div>
    </div>
</div>

<div class="modal fade" id="editRecord" tabindex="-1" role="dialog"
     aria-labelledby="editRecordTitle"
     aria-hidden="true">
    <div class="modal-dialog modal-dialog-centered" role="document">
        <div class="modal-content">
            <div class="modal-header">
                <h4 class="modal-title" id="editRecordTitle">Edit record</h4>
                <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                    <span aria-hidden="true">&times;</span>
                </button>
            </div>
            <div class="modal-body">
                <div class="form-group row">
                    <form id="editRecordForm" method="post">
                        <input type="hidden" id="rec_voc_id" name="voc_id">
                        <input type="hidden" id="edit_record_id" name="record_id">
                        <input type="hidden" id="edit_is_exp" name="is_exp">
                        <input type="hidden" id="rec_voc_exp" name="voc_exp">
                        <input type="hidden" id="rec_action_name" name="action_name">
                        <label for="edit_word_text" class="col-sm-2 col-form-label">Word</label>
                        <div class="col-sm-10">
                            <input required="required" type="text" class="form-control"
                                   id="edit_word_text" name="word_text">
                        </div>
                        <label for="edit_word_text2" class="col-sm-2 col-form-label">Definition</label>
                        <div class="col-sm-10">
                            <input required="required" type="text" class="form-control"
                                   id="edit_word_text2" name="word2_text">
                        </div>
                    </form>
                </div>
            </div>
            <div class="modal-footer">
                <button type="submit" class="btn btn-primary" form="editRecordForm">OK</button>
                <button type="reset" class="btn btn-secondary" data-dismiss="modal"
                        form="editRecordForm">Cancel
                </button>
            </div>
        </div>
    </div>
</div>