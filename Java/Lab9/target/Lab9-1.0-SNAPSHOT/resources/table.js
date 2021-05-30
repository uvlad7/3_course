$(document).ready(function () {
    $('#main_table').on('click', '.clickable-row', function (event) {
        if (!$(this).hasClass('selected-row')) {
            $('.selected-row').removeClass('selected-row');
            $(this).addClass('selected-row');
            $('#word_id').val($(this).attr('data-word_id'));
            $('#record_id').val($(this).attr('data-record_id'));
            $('#is_exp').val($(this).attr('data-is_exp'));
        }
    });
    $("#delete_word").click(function () {
        if ($('#record_id').val()) {
            $('#action_name').val('delete_word');
            $('#actionsTableForm').submit();
        } else {
            $('#recordNotSelectedTitle').html('Record not selected');
            $('#recordNotSelected').modal('show');
        }
    });
    $("#delete_record").click(function () {
        if ($('#record_id').val()) {
            $('#action_name').val('delete_record');
            $('#actionsTableForm').submit();
        } else {
            $('#recordNotSelectedTitle').html('Record not selected');
            $('#recordNotSelected').modal('show');
        }
    });
    $("#delete_vocabulary").click(function () {
        if ($('#voc_id').val()) {
            $('#action_name').val('delete_vocabulary');
            $('#actionsTableForm').submit();
        } else {
            $('#recordNotSelectedTitle').html('Vocabulary not selected');
            $('#recordNotSelected').modal('show');
        }
    });
    $("#add_vocabulary").click(function () {
        $('#edit_action_name').val('add_vocabulary');
        $('#editVocTitle').html('Add vocabulary');
        $('#editVoc').modal('show');
    });
    $("#edit_vocabulary").click(function () {
        if ($('#voc_id').val()) {
            $('#edit_action_name').val('edit_vocabulary');
            $('#editVocTitle').html('Edit vocabulary');
            $('#edit_voc_id').val($('#voc_id').val());
            $('#edit_voc_exp').val($('#voc_exp').val() === 'true');
            $('#edit_voc_exp').attr('checked', $('#voc_exp').val() === 'true');
            $('#voc_name').val($('#avoc_' + $('#voc_id').val()).text());
            $('#editVoc').modal('show');
        } else {
            $('#recordNotSelectedTitle').html('Vocabulary not selected');
            $('#recordNotSelected').modal('show');
        }
    });
    $("#edit_voc_exp").on('change', function () {
        if ($(this).is(':checked')) {
            $(this).attr('value', 'true');
        } else {
            $(this).attr('value', 'false');
        }
    });
    $("#add_word").click(function () {
        $('#word_action_name').val('add_word');
        $('#editWordTitle').html('Add word');
        $('#editWord').modal('show');
    });
    $("#edit_word").click(function () {
        if ($('#record_id').val()) {
            $('#word_action_name').val('edit_word');
            $('#editWordTitle').html('Edit word');
            $('#edit_word_id').val($('#word_id').val());
            $('#word_text').val($('#word_' + $('#record_id').val() + '_' + $('#is_exp').val()).text());
            $('#editWord').modal('show');
        } else {
            $('#recordNotSelectedTitle').html('Record not selected');
            $('#recordNotSelected').modal('show');
        }
    });
    $("#add_record").click(function () {
        if ($('#voc_id').val()) {
            $('#rec_action_name').val('add_record');
            $('#editRecordTitle').html('Add record');
            $('#rec_voc_id').val($('#voc_id').val());
            $('#rec_voc_exp').val($('#voc_exp').val());
            $('#editRecord').modal('show');
        } else {
            $('#recordNotSelectedTitle').html('Vocabulary not selected');
            $('#recordNotSelected').modal('show');
        }
    });
    $("#edit_record").click(function () {
        if ($('#record_id').val()) {
            $('#rec_action_name').val('edit_record');
            $('#editRecordTitle').html('Edit record');
            $('#edit_record_id').val($('#record_id').val());
            $('#edit_record_id').val($('#record_id').val());
            $('#edit_word_text').val($('#word_' + $('#record_id').val() + '_' + $('#is_exp').val()).text());
            $('#edit_word_text2').val($('#word2_' + $('#record_id').val() + '_' + $('#is_exp').val()).text());
            $('#editRecord').modal('show');
        } else {
            $('#recordNotSelectedTitle').html('Record not selected');
            $('#recordNotSelected').modal('show');
        }
    });
});