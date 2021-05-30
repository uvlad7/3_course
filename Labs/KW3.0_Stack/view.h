#pragma once
#include "kr.h"
#include "stdafx.h"

extern HINSTANCE hInst;
INT_PTR CALLBACK	DlgMain(HWND hDlg, UINT message, WPARAM wParam, LPARAM lParam);
INT_PTR CALLBACK	DlgPush(HWND hDlg, UINT message, WPARAM wParam, LPARAM lParam);
INT_PTR CALLBACK	DlgPop(HWND hDlg, UINT message, WPARAM wParam, LPARAM lParam);
INT_PTR CALLBACK	DlgTop(HWND hDlg, UINT message, WPARAM wParam, LPARAM lParam);
INT_PTR CALLBACK	DlgClear(HWND hDlg, UINT message, WPARAM wParam, LPARAM lParam);

enum class DialogType { Push, Top, Pop, Clear };

class View {
public:
	void Paint(HWND hDlg, HFONT hFont);

	void MakeDialog(HINSTANCE hInst, HWND hDlg, DialogType type);

	void PrintText(HWND hDlg, int ID, const char* outBuff);

	void Line(HDC hdc, int X0, int Y0, int X, int Y);

	std::string str_to_out_iter, str_for_first, str_for_last, len;
};