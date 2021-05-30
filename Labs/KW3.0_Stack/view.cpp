#include "stdafx.h"
#include "view.h"

void View::Paint(HWND hDlg, HFONT hFont) {
	PAINTSTRUCT ps;
	HDC hdc;
	hdc = BeginPaint(hDlg, &ps);
	SelectObject(hdc, hFont);
	PrintText(hDlg, IDC_STATIC4, str_to_out_iter.c_str());
	PrintText(hDlg, IDC_STATIC2, str_for_first.c_str());
	PrintText(hDlg, IDC_STATIC3, str_for_last.c_str());
	PrintText(hDlg, IDC_STATIC5, len.c_str());
	EndPaint(hDlg, &ps);
}

void View::MakeDialog(HINSTANCE hInst, HWND hDlg, DialogType type) {
	switch (type)
	{
	case DialogType::Push:
		DialogBox(hInst, MAKEINTRESOURCE(IDD_ADD), hDlg, DlgPush);
		break;
	case DialogType::Top:
		DialogBox(hInst, MAKEINTRESOURCE(IDD_GET), hDlg, DlgTop);
		break;
	case DialogType::Pop:
		DialogBox(hInst, MAKEINTRESOURCE(IDD_DEL), hDlg, DlgPop);
		break;
	case DialogType::Clear:
		DialogBox(hInst, MAKEINTRESOURCE(IDD_DEL), hDlg, DlgClear);
		break;
	default:
		break;
	}
}

void View::PrintText(HWND hDlg, int ID, const char* outBuff) {
	SetDlgItemText(hDlg, ID, outBuff);
}

void View::Line(HDC hdc, int X0, int Y0, int X, int Y) {
	MoveToEx(hdc, X0, Y0, NULL);
	LineTo(hdc, X, Y);
}
