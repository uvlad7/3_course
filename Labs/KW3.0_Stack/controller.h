#pragma once
#include <memory>
#include "view.h"
#include "model.h"
#include "complex.h"

class Controller : public Observer<Complex> {
public:

	Controller(Stack<Complex>* model) : view_(), model_(model) { model_->attach(this); }

	~Controller() { model_->detach(this); }

	int Start(HINSTANCE hInstance, HINSTANCE hPrevInstance, LPWSTR lpCmdLine, int nCmdShow) {
		UNREFERENCED_PARAMETER(hPrevInstance);
		UNREFERENCED_PARAMETER(lpCmdLine);

		if (!InitInstance(hInstance, nCmdShow))
		{
			return FALSE;
		}

		MSG msg;
		while (GetMessage(&msg, nullptr, 0, 0))
		{
			TranslateMessage(&msg);
			DispatchMessage(&msg);
		}

		return (int)msg.wParam;
	}

	void Main(HWND hDlg, UINT message, WPARAM wParam, LPARAM lParam, HFONT hFont) {
		std::string str;
		switch (message)
		{
		case WM_COMMAND:
			switch (LOWORD(wParam)) {
			case IDC_BUTTON_1: // Push
				view_.MakeDialog(hInst, hDlg, DialogType::Push);
				break;
			case IDC_BUTTON_3: // Pop
				if (model_->IsEmpty()) {
					MessageBox(hDlg, "Can't pop element from empty model", "Pop", 0);
				}
				else {
					view_.MakeDialog(hInst, hDlg, DialogType::Pop);
				}
				break;
			case IDC_BUTTON_4: // Get
				if (model_->IsEmpty()) {
					MessageBox(hDlg, "Can't get element from empty model", "Top", 0);
				}
				else {
					view_.MakeDialog(hInst, hDlg, DialogType::Top);
				}
				break;
			case IDC_BUTTON_5: // Clear
				view_.MakeDialog(hInst, hDlg, DialogType::Clear);
				break;
			case WM_DESTROY:
				DeleteObject(hFont);
				PostQuitMessage(0);
				break;
			}
			break;
		case WM_PAINT:
			view_.Paint(hDlg, hFont);
			break;
		}
	}

	void Push(HWND hDlg, UINT message, WPARAM wParam, LPARAM lParam) {
		char buff1[100] = ""; // here I put text from dialog box
		char buff2[100] = ""; // here I put text from dialog box
		switch (LOWORD(wParam)) {
		case IDOK:
			GetDlgItemTextA(hDlg, IDC_EDIT1, buff1, 100);
			GetDlgItemTextA(hDlg, IDC_EDIT2, buff2, 100);
			model_->Push(Complex(atoi(buff1), atoi(buff2)));
		case WM_DESTROY:
			EndDialog(hDlg, LOWORD(wParam));
			break;
		}
	}

	void Pop(HWND hDlg, UINT message, WPARAM wParam, LPARAM lParam) {
		switch (LOWORD(wParam)) {
		case IDOK:
			model_->Pop();
		case WM_DESTROY:
			EndDialog(hDlg, LOWORD(wParam));
			break;
		}
	}

	void Get(HWND hDlg, UINT message, WPARAM wParam, LPARAM lParam) {
		switch (LOWORD(wParam)) {
		case IDC_BUTTON_1: //Top
			view_.PrintText(hDlg, IDC_STATIC1, to_string(model_->Top()).c_str());
			break;
		case IDC_BUTTON_2: //Bottom
			view_.PrintText(hDlg, IDC_STATIC1, to_string(model_->Bottom()).c_str());
			break;
		case WM_DESTROY:
			EndDialog(hDlg, LOWORD(wParam));
			break;
		}
	}

	void Clear(HWND hDlg, UINT message, WPARAM wParam, LPARAM lParam) {
		switch (LOWORD(wParam)) {
		case IDOK:
			model_->Clear();
		case WM_DESTROY:
			EndDialog(hDlg, LOWORD(wParam));
			break;
		}
	}

	const static HFONT hFont;

	void update(Stack<Complex>* theChangedSubject) override {
		if (theChangedSubject == model_) {
			view_.len = to_string(model_->Size());
			if (model_->Size() > 0) {
				MaxVisitor<Complex> max_visitor;
				MinVisitor<Complex> min_visitor;
				view_.str_to_out_iter = "";
				for (auto iter = model_->begin(); iter != model_->end(); ++iter) {
					view_.str_to_out_iter += to_string(*iter);
					view_.str_to_out_iter += '\n';
				}
				view_.str_for_first = to_string(model_->Accept(max_visitor)); // output of first visitor
				view_.str_for_last = to_string(model_->Accept(min_visitor)); // output of last visitor
			}
			else {
				view_.str_to_out_iter = "";
				view_.str_for_first = "";
				view_.str_for_last = "";
			}
		}
	}

private:
	BOOL InitInstance(HINSTANCE hInstance, int nCmdShow) {
		hInst = hInstance;

		HWND hWnd = CreateDialog(hInstance, MAKEINTRESOURCE(IDD_MAIN), NULL, DlgMain);

		if (!hWnd)
		{
			return FALSE;
		}

		ShowWindow(hWnd, nCmdShow);
		UpdateWindow(hWnd);

		return TRUE;
	}

	HWND hMainDlg;
	View view_;
	Stack<Complex>* model_;
};

const HFONT Controller::hFont = CreateFont(20, 0, 0, 0,
	FW_DONTCARE,
	FALSE, FALSE, FALSE, // курсив подчеркивание зачеркивание (прост поставить true)
	DEFAULT_CHARSET, OUT_OUTLINE_PRECIS,
	CLIP_DEFAULT_PRECIS, CLEARTYPE_QUALITY,
	VARIABLE_PITCH, TEXT("Areal")); // в кавычках можно указать какой-нибудь шрифт
