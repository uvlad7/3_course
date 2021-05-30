// KR.cpp: определяет точку входа для консольного приложения.
//
#define _CRT_SECURE_NO_WARNINGS

#include <windows.h>
#include "resource.h"
#include "KList.h"
#include <string>
using namespace std;

LRESULT CALLBACK WndProc(HWND, UINT, WPARAM, LPARAM);
KList *model = nullptr;
Controller *controller = nullptr;

int WINAPI WinMain(HINSTANCE hInstance, HINSTANCE hPrevInstance, LPSTR lpszCmdLine, int nCmdShow)
{
	HWND hWnd;
	MSG lpMsg;

	hWnd = CreateDialog(hInstance, MAKEINTRESOURCE(IDD_DIALOG1), NULL, (DLGPROC)WndProc);

	ShowWindow(hWnd, nCmdShow);
	UpdateWindow(hWnd);
	while (GetMessage(&lpMsg, NULL, 0, 0))
	{
		TranslateMessage(&lpMsg);
		DispatchMessage(&lpMsg);
	}
	return(lpMsg.wParam);
}

LRESULT CALLBACK WndProc(HWND hWnd, UINT messg, WPARAM wParam, LPARAM lParam)
{
	switch (messg)
	{
	case WM_INITDIALOG:
	{
		SetWindowText(GetDlgItem(hWnd, IDC_EDIT1), "0");
		SetWindowText(GetDlgItem(hWnd, IDC_EDIT3), "0");
		model = new KList(10);
		controller = new Controller(model, GetDlgItem(hWnd, IDC_EDIT1), GetDlgItem(hWnd, IDC_EDIT3), GetDlgItem(hWnd, IDC_EDIT2),
			GetDlgItem(hWnd, IDPOPFRONT), GetDlgItem(hWnd, IDPOPBACK), GetDlgItem(hWnd, IDPUSH));
		break;
	}
	case WM_COMMAND:
	{
		controller->translate(LOWORD(wParam));
		break;
	}
	case WM_DESTROY:
	{
		delete controller;
		delete model;
		PostQuitMessage(0);
		break;
	}
	default:
		return(DefWindowProc(hWnd, messg, wParam, lParam));
	}
	return 0;
}