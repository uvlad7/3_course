#include "stdafx.h"
#include "kr.h"
#include <ctime>
#include <iterator>
#include "controller.h"
#include "model.h"

using std::vector;
using std::wstring;
using std::to_wstring;

#define MAX_LOADSTRING 100

HINSTANCE hInst;
INT_PTR CALLBACK	DlgMain(HWND hDlg, UINT message, WPARAM wParam, LPARAM lParam);
INT_PTR CALLBACK	DlgPush(HWND hDlg, UINT message, WPARAM wParam, LPARAM lParam);
INT_PTR CALLBACK	DlgPop(HWND hDlg, UINT message, WPARAM wParam, LPARAM lParam);
INT_PTR CALLBACK	DlgTop(HWND hDlg, UINT message, WPARAM wParam, LPARAM lParam);
INT_PTR CALLBACK	DlgClear(HWND hDlg, UINT message, WPARAM wParam, LPARAM lParam);

Stack<Complex> v;
Controller controller(&v);

// Без этих функций WinApi не запускается
int APIENTRY wWinMain(_In_ HINSTANCE hInstance,
                     _In_opt_ HINSTANCE hPrevInstance,
                     _In_ LPWSTR    lpCmdLine,
                     _In_ int       nCmdShow)
{
	return controller.Start(hInstance, hPrevInstance, lpCmdLine, nCmdShow);
}

INT_PTR CALLBACK DlgMain(HWND hDlg, UINT message, WPARAM wParam, LPARAM lParam) {
	UNREFERENCED_PARAMETER(lParam);
	switch (message)
	{
	case WM_INITDIALOG:
		return (INT_PTR)TRUE;
	case WM_COMMAND:
	case WM_PAINT:
		controller.Main(hDlg, message, wParam, lParam, Controller::hFont);
		break;
	}
	return (INT_PTR)FALSE;
}

INT_PTR CALLBACK DlgPush(HWND hDlg, UINT message, WPARAM wParam, LPARAM lParam)
{
	UNREFERENCED_PARAMETER(lParam);
	switch (message)
	{
	case WM_INITDIALOG:
		return (INT_PTR)TRUE;

	case WM_COMMAND:
		controller.Push(hDlg, message, wParam, lParam);
		break;
	}
	return (INT_PTR)FALSE;
}

INT_PTR CALLBACK DlgPop(HWND hDlg, UINT message, WPARAM wParam, LPARAM lParam)
{
	UNREFERENCED_PARAMETER(lParam);
	switch (message)
	{
	case WM_INITDIALOG:
		return (INT_PTR)TRUE;

	case WM_COMMAND:
		controller.Pop(hDlg, message, wParam, lParam);
		break;
	}
	return (INT_PTR)FALSE;
}

INT_PTR CALLBACK DlgTop(HWND hDlg, UINT message, WPARAM wParam, LPARAM lParam)
{
	UNREFERENCED_PARAMETER(lParam);
	switch (message)
	{
	case WM_INITDIALOG:
		return (INT_PTR)TRUE;

	case WM_COMMAND:
		controller.Get(hDlg, message, wParam, lParam);
		break;
	}
	return (INT_PTR)FALSE;
}

INT_PTR CALLBACK DlgClear(HWND hDlg, UINT message, WPARAM wParam, LPARAM lParam)
{
	UNREFERENCED_PARAMETER(lParam);
	switch (message)
	{
	case WM_INITDIALOG:
		return (INT_PTR)TRUE;

	case WM_COMMAND:
		controller.Clear(hDlg, message, wParam, lParam);
		break;
	}
	return (INT_PTR)FALSE;
}
